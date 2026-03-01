package com.aiflow.workflow.ui.workflow

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiflow.workflow.data.model.ParameterDefinition
import com.aiflow.workflow.data.model.ParameterType
import com.aiflow.workflow.data.model.TaskSettings
import com.aiflow.workflow.data.model.TaskSubmitRequest

@Composable
fun ParameterInputScreen(
    workflowId: String,
    onBackClick: () -> Unit,
    onSubmitSuccess: (String) -> Unit, // taskId
    viewModel: WorkflowViewModel = hiltViewModel()
) {
    val detailState by viewModel.workflowDetailState.collectAsState()
    val taskSubmitState by viewModel.taskSubmitState.collectAsState()
    
    // 加载工作流详情（如果还没加载）
    LaunchedEffect(workflowId) {
        if (detailState.workflow == null || detailState.workflow?.id != workflowId) {
            viewModel.loadWorkflowDetail(workflowId)
        }
    }
    
    // 监听提交成功
    LaunchedEffect(taskSubmitState.submittedTask) {
        taskSubmitState.submittedTask?.let { response ->
            onSubmitSuccess(response.taskId)
        }
    }
    
    Scaffold(
        topBar = {
            InputTopAppBar(
                title = detailState.workflow?.name ?: "参数输入",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                detailState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                detailState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "错误",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "加载参数失败",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = detailState.error,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                detailState.workflow != null -> {
                    ParameterInputContent(
                        workflow = detailState.workflow!!,
                        isLoading = taskSubmitState.isLoading,
                        error = taskSubmitState.error,
                        onSubmit = { parameters ->
                            val request = TaskSubmitRequest(
                                workflowId = workflowId,
                                parameters = parameters,
                                settings = TaskSettings(
                                    priority = "normal",
                                    notifyWhenComplete = true
                                )
                            )
                            viewModel.submitTask(request)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputTopAppBar(
    title: String,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "返回"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
private fun ParameterInputContent(
    workflow: WorkflowDetail,
    isLoading: Boolean,
    error: String?,
    onSubmit: (Map<String, Any>) -> Unit
) {
    val parameters = workflow.parameterDefinition
    var formValues by remember { mutableStateOf(mutableMapOf<String, Any>()) }
    var formErrors by remember { mutableStateOf(mutableMapOf<String, String>()) }
    
    // 初始化默认值
    LaunchedEffect(parameters) {
        parameters.forEach { param ->
            if (param.defaultValue != null) {
                formValues[param.key] = param.defaultValue
            }
        }
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 16.dp,
            vertical = 16.dp
        )
    ) {
        item {
            Text(
                text = "请填写以下参数",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "工作流：${workflow.name}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // 参数输入区域
        items(parameters) { param ->
            ParameterInputField(
                parameter = param,
                value = formValues[param.key],
                error = formErrors[param.key],
                onValueChange = { newValue ->
                    formValues[param.key] = newValue
                    // 清除该字段的错误
                    formErrors.remove(param.key)
                }
            )
        }
        
        // 错误提示
        if (error != null) {
            item {
                ErrorMessage(message = error)
            }
        }
        
        // 提交按钮
        item {
            SubmitButton(
                isLoading = isLoading,
                basePoints = workflow.basePoints,
                onClick = {
                    // 验证表单
                    val errors = validateForm(parameters, formValues)
                    formErrors.clear()
                    formErrors.putAll(errors)
                    
                    if (errors.isEmpty()) {
                        onSubmit(formValues.toMap())
                    }
                }
            )
        }
    }
}

@Composable
private fun ParameterInputField(
    parameter: ParameterDefinition,
    value: Any?,
    error: String?,
    onValueChange: (Any) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 参数标题
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = parameter.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                
                if (parameter.required) {
                    Text(
                        text = "必填",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFF4D4F),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 参数描述（如果有）
            if (!parameter.description.isNullOrEmpty()) {
                Text(
                    text = parameter.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // 根据类型渲染不同的输入组件
            when (parameter.type) {
                ParameterType.TEXT -> {
                    TextInputField(
                        value = value?.toString() ?: "",
                        onValueChange = { onValueChange(it) },
                        placeholder = "请输入文本",
                        error = error,
                        isRequired = parameter.required
                    )
                }
                
                ParameterType.NUMBER -> {
                    NumberInputField(
                        value = value?.toString() ?: "",
                        onValueChange = { onValueChange(it) },
                        placeholder = "请输入数字",
                        error = error,
                        isRequired = parameter.required,
                        constraints = parameter.constraints
                    )
                }
                
                ParameterType.SELECT -> {
                    SelectInputField(
                        value = value?.toString(),
                        options = parameter.options ?: emptyList(),
                        onValueChange = { onValueChange(it) },
                        placeholder = "请选择",
                        error = error,
                        isRequired = parameter.required
                    )
                }
                
                ParameterType.MULTI_SELECT -> {
                    MultiSelectInputField(
                        values = (value as? List<String>) ?: emptyList(),
                        options = parameter.options ?: emptyList(),
                        onValueChange = { onValueChange(it) },
                        error = error,
                        isRequired = parameter.required
                    )
                }
                
                ParameterType.BOOLEAN -> {
                    BooleanInputField(
                        value = value as? Boolean ?: false,
                        onValueChange = { onValueChange(it) },
                        error = error
                    )
                }
                
                ParameterType.SLIDER -> {
                    SliderInputField(
                        value = value?.toString()?.toFloatOrNull() ?: 0f,
                        onValueChange = { onValueChange(it.toString()) },
                        constraints = parameter.constraints,
                        error = error,
                        isRequired = parameter.required
                    )
                }
                
                ParameterType.IMAGE_FILE,
                ParameterType.VIDEO_FILE,
                ParameterType.AUDIO_FILE -> {
                    FileInputField(
                        value = value?.toString(),
                        fileType = parameter.type,
                        constraints = parameter.constraints,
                        onValueChange = { onValueChange(it) },
                        error = error,
                        isRequired = parameter.required
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TextInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String?,
    isRequired: Boolean
) {
    val focusManager = LocalFocusManager.current
    
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            singleLine = false,
            maxLines = 4,
            isError = error != null,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                textColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(12.dp)
        )
        
        if (error != null) {
            ErrorText(message = error)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NumberInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String?,
    isRequired: Boolean,
    constraints: ParameterConstraints?
) {
    val focusManager = LocalFocusManager.current
    
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                // 只允许数字输入
                if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                    onValueChange(newValue)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            singleLine = true,
            isError = error != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                textColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = {
                if (constraints?.min != null || constraints?.max != null) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "范围",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            trailingIcon = {
                if (error != null) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "错误",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
        
        // 显示范围提示
        constraints?.let {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                it.min?.let { min ->
                    Text(
                        text = "最小值: $min",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                it.max?.let { max ->
                    Text(
                        text = "最大值: $max",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        if (error != null) {
            ErrorText(message = error)
        }
    }
}

@Composable
private fun SelectInputField(
    value: String?,
    options: List<ParameterOption>,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String?,
    isRequired: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value?.let { selectedValue ->
                        options.find { it.value == selectedValue }?.label ?: selectedValue
                    } ?: placeholder,
                    color = if (value != null) MaterialTheme.colorScheme.onSurface
                           else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontSize = 16.sp
                )
                
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                                 else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "收起" else "展开",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.label,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        onValueChange(option.value)
                        expanded = false
                    }
                )
            }
        }
        
        if (error != null) {
            ErrorText(message = error)
        }
    }
}

@Composable
private fun MultiSelectInputField(
    values: List<String>,
    options: List<ParameterOption>,
    onValueChange: (List<String>) -> Unit,
    error: String?,
    isRequired: Boolean
) {
    Column {
        Text(
            text = "请选择（可多选）",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        val newValues = if (values.contains(option.value)) {
                            values - option.value
                        } else {
                            values + option.value
                        }
                        onValueChange(newValues)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = values.contains(option.value),
                    onCheckedChange = { checked ->
                        val newValues = if (checked) {
                            values + option.value
                        } else {
                            values - option.value
                        }
                        onValueChange(newValues)
                    }
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = option.label,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp
                )
            }
        }
        
        if (error != null) {
            ErrorText(message = error)
        }
    }
}

@Composable
private fun BooleanInputField(
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    error: String?
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "是否启用",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
            )
            
            Switch(
                checked = value,
                onCheckedChange = onValueChange
            )
        }
        
        if (error != null) {
            ErrorText(message = error)
        }
    }
}

@Composable
private fun SliderInputField(
    value: Float,
    onValueChange: (Float) -> Unit,
    constraints: ParameterConstraints?,
    error: String?,
    isRequired: Boolean
) {
    val min = constraints?.min?.toFloatOrNull() ?: 0f
    val max = constraints?.max?.toFloatOrNull() ?: 100f
    
    Column {
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = min..max,
            steps = 19, // 20个刻度
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "当前值: ${value.toInt()}",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = "${min.toInt()} - ${max.toInt()}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
        
        if (error != null) {
            ErrorText(message = error)
        }
    }
}

@Composable
private fun FileInputField(
    value: String?,
    fileType: ParameterType,
    constraints: ParameterConstraints?,
    onValueChange: (String) -> Unit,
    error: String?,
    isRequired: Boolean
) {
    var showFilePicker by remember { mutableStateOf(false) }
    
    Column {
        // 文件选择按钮
        Button(
            onClick = { showFilePicker = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "选择文件",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (fileType) {
                        ParameterType.IMAGE_FILE -> "选择图片文件"
                        ParameterType.VIDEO_FILE -> "选择视频文件"
                        ParameterType.AUDIO_FILE -> "选择音频文件"
                        else -> "选择文件"
                    }
                )
            }
        }
        
        // 选中的文件显示
        if (!value.isNullOrEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = value,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    IconButton(
                        onClick = { onValueChange("") },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "清除",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
        
        // 文件约束提示
        constraints?.let {
            Column(
                modifier = Modifier.padding(top = 4.dp)
            ) {
                it.maxSize?.let { maxSize ->
                    Text(
                        text = "最大文件大小: $maxSize",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                it.formats?.let { formats ->
                    Text(
                        text = "支持格式: ${formats.joinToString(", ")}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        if (error != null) {
            ErrorText(message = error)
        }
    }
}

@Composable
private fun ErrorText(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "错误",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun ErrorMessage(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "错误",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun SubmitButton(
    isLoading: Boolean,
    basePoints: Int,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "提交",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "提交任务",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "预计消耗: $basePoints 资源点",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// 表单验证函数
private fun validateForm(
    parameters: List<ParameterDefinition>,
    values: Map<String, Any>
): Map<String, String> {
    val errors = mutableMapOf<String, String>()
    
    parameters.forEach { param ->
        val value = values[param.key]
        
        // 必填验证
        if (param.required && (value == null || 
            (value is String && value.isBlank()) ||
            (value is List<*> && value.isEmpty()))) {
            errors[param.key] = "${param.name}是必填项"
            return@forEach
        }
        
        if (value != null) {
            // 类型特定验证
            when (param.type) {
                ParameterType.NUMBER -> {
                    val strValue = value.toString()
                    if (strValue.isNotBlank()) {
                        val numValue = strValue.toDoubleOrNull()
                        if (numValue == null) {
                            errors[param.key] = "${param.name}必须是有效数字"
                        } else {
                            // 范围验证
                            param.constraints?.let { constraints ->
                                constraints.min?.toDoubleOrNull()?.let { min ->
                                    if (numValue < min) {
                                        errors[param.key] = "${param.name}不能小于$min"
                                    }
                                }
                                constraints.max?.toDoubleOrNull()?.let { max ->
                                    if (numValue > max) {
                                        errors[param.key] = "${param.name}不能大于$max"
                                    }
                                }
                            }
                        }
                    }
                }
                
                ParameterType.SELECT -> {
                    val strValue = value.toString()
                    if (strValue.isNotBlank()) {
                        val validOptions = param.options?.map { it.value } ?: emptyList()
                        if (!validOptions.contains(strValue)) {
                            errors[param.key] = "请选择有效的${param.name}"
                        }
                    }
                }
                
                ParameterType.MULTI_SELECT -> {
                    val listValue = value as? List<*>
                    listValue?.let { list ->
                        val validOptions = param.options?.map { it.value } ?: emptyList()
                        list.forEach { item ->
                            if (!validOptions.contains(item.toString())) {
                                errors[param.key] = "请选择有效的${param.name}选项"
                                return@let
                            }
                        }
                    }
                }
                
                ParameterType.SLIDER -> {
                    val floatValue = value.toString().toFloatOrNull()
                    if (floatValue == null) {
                        errors[param.key] = "${param.name}必须是有效数值"
                    } else {
                        param.constraints?.let { constraints ->
                            constraints.min?.toFloatOrNull()?.let { min ->
                                if (floatValue < min) {
                                    errors[param.key] = "${param.name}不能小于$min"
                                }
                            }
                            constraints.max?.toFloatOrNull()?.let { max ->
                                if (floatValue > max) {
                                    errors[param.key] = "${param.name}不能大于$max"
                                }
                            }
                        }
                    }
                }
                
                else -> {
                    // 其他类型暂不验证
                }
            }
        }
    }
    
    return errors
}