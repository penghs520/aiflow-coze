<template>
  <div class="workflow-list">
    <h2>工作流管理</h2>
    <el-card>
      <div class="toolbar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索工作流"
          style="width: 300px"
          clearable
          @clear="handleSearch"
        >
          <template #append>
            <el-button icon="Search" @click="handleSearch" />
          </template>
        </el-input>
        <div>
          <el-button type="primary" icon="Plus" @click="handleCreate">新增工作流</el-button>
          <el-button
            type="success"
            icon="Upload"
            :disabled="!selectedIds.length"
            @click="handleBatchPublish"
          >
            批量上架
          </el-button>
          <el-button
            type="warning"
            icon="Download"
            :disabled="!selectedIds.length"
            @click="handleBatchUnpublish"
          >
            批量下架
          </el-button>
        </div>
      </div>

      <el-table
        :data="tableData"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        style="margin-top: 20px"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="280" />
        <el-table-column prop="name" label="名称" min-width="150" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="basePoints" label="资源点" width="100" />
        <el-table-column prop="usageCount" label="使用次数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'warning' : 'info'">
              {{ row.status === 1 ? '上架' : row.status === 2 ? '内测' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="success" @click="handleExecute(row)">执行</el-button>
            <el-button
              link
              :type="row.status === 1 ? 'warning' : 'success'"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 执行对话框 -->
    <el-dialog
      v-model="executeDialogVisible"
      title="执行工作流"
      width="600px"
    >
      <el-form :model="executeForm" ref="executeFormRef" label-width="140px" label-position="left">
        <el-form-item label="工作流" class="static-info">
          <span>{{ executeForm.workflowName }}</span>
        </el-form-item>

        <el-form-item label="Coze ID" class="static-info">
          <span>{{ executeForm.cozeWorkflowId }}</span>
        </el-form-item>

        <!-- 动态参数表单 -->
        <template v-if="executeForm.parameterDefinition && executeForm.parameterDefinition.length > 0">
          <el-divider>参数配置</el-divider>
          <el-form-item
            v-for="param in executeForm.parameterDefinition"
            :key="param.key"
            :label="param.key"
            :required="param.required"
            :prop="'parameters.' + param.key"
            v-show="param.key !== 'mihe_key'"
          >
            <!-- 文本输入 -->
            <el-input
              v-if="param.type === 'text'"
              v-model="executeForm.parameters[param.key]"
              :placeholder="param.description || '请输入' + param.key"
            />

            <!-- 数字输入 -->
            <template v-else-if="param.type === 'number'">
              <div class="number-input-wrapper">
                <el-input-number
                  v-model="executeForm.parameters[param.key]"
                  :min="param.constraints?.min"
                  :max="param.constraints?.max"
                />
                <div v-if="param.description" class="param-hint">{{ param.description }}</div>
              </div>
            </template>

            <!-- 下拉选择 -->
            <el-select
              v-else-if="param.type === 'select'"
              v-model="executeForm.parameters[param.key]"
              :placeholder="param.description || '请选择'"
              style="width: 100%"
            >
              <el-option
                v-for="option in param.options"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </el-select>

            <!-- 多选 -->
            <el-select
              v-else-if="param.type === 'multi_select'"
              v-model="executeForm.parameters[param.key]"
              multiple
              :placeholder="param.description || '请选择'"
              style="width: 100%"
            >
              <el-option
                v-for="option in param.options"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </el-select>

            <!-- 布尔值 -->
            <el-switch
              v-else-if="param.type === 'boolean'"
              v-model="executeForm.parameters[param.key]"
              :active-text="'是'"
              :inactive-text="'否'"
            />

            <!-- 滑块 -->
            <el-slider
              v-else-if="param.type === 'slider'"
              v-model="executeForm.parameters[param.key]"
              :min="param.constraints?.min || 0"
              :max="param.constraints?.max || 100"
            />

            <!-- 图片上传 -->
            <div v-else-if="param.type === 'image_file'" class="upload-wrapper">
              <el-upload
                class="upload-demo"
                :action="uploadImageUrl"
                :headers="uploadHeaders"
                :show-file-list="false"
                :on-success="(response) => handleParamFileSuccess(param.key, response)"
                :before-upload="beforeImageUpload"
                accept="image/*"
              >
                <el-button type="primary">选择图片</el-button>
              </el-upload>
              <div v-if="executeForm.parameters[param.key]" class="file-name">{{ executeForm.parameters[param.key].split('/').pop() }}</div>
              <div v-else-if="param.description" class="file-desc">{{ param.description }}</div>
            </div>

            <!-- 视频上传 -->
            <div v-else-if="param.type === 'video_file'" class="upload-wrapper">
              <el-upload
                class="upload-demo"
                :action="uploadVideoUrl"
                :headers="uploadHeaders"
                :show-file-list="false"
                :on-success="(response) => handleParamFileSuccess(param.key, response)"
                :before-upload="beforeVideoUpload"
                accept="video/*"
              >
                <el-button type="primary">选择视频</el-button>
              </el-upload>
              <div v-if="executeForm.parameters[param.key]" class="file-name">{{ executeForm.parameters[param.key].split('/').pop() }}</div>
              <div v-else-if="param.description" class="file-desc">{{ param.description }}</div>
            </div>

            <!-- 音频上传 -->
            <div v-else-if="param.type === 'audio_file'" class="upload-wrapper">
              <el-upload
                class="upload-demo"
                :action="uploadFileUrl"
                :headers="uploadHeaders"
                :show-file-list="false"
                :on-success="(response) => handleParamFileSuccess(param.key, response)"
                accept="audio/*"
              >
                <el-button type="primary">选择音频</el-button>
              </el-upload>
              <div v-if="executeForm.parameters[param.key]" class="file-name">{{ executeForm.parameters[param.key].split('/').pop() }}</div>
              <div v-else-if="param.description" class="file-desc">{{ param.description }}</div>
            </div>

            <!-- 默认文本输入 -->
            <el-input
              v-else
              v-model="executeForm.parameters[param.key]"
              :placeholder="param.description || '请输入' + param.key"
            />
          </el-form-item>
        </template>

        <!-- 无参数提示 -->
        <el-alert
          v-else
          title="该工作流无需配置参数"
          type="info"
          :closable="false"
          style="margin-bottom: 20px"
        />
      </el-form>

      <template #footer>
        <el-button @click="executeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleExecuteSubmit" :loading="executeSubmitting">执行</el-button>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="Coze工作流ID" prop="cozeWorkflowId">
          <el-input v-model="form.cozeWorkflowId" placeholder="请输入Coze工作流ID" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input v-model="form.category" />
        </el-form-item>
        <el-form-item label="资源点" prop="pointsCost">
          <el-input-number v-model="form.pointsCost" :min="0" />
        </el-form-item>
        <el-form-item label="预览封面图" prop="coverUrl">
          <el-upload
            class="cover-uploader"
            :action="uploadImageUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :before-upload="beforeImageUpload"
            accept="image/*"
          >
            <img v-if="form.coverUrl" :src="form.coverUrl" class="cover-image" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="效果视频" prop="previewVideoUrl">
          <el-upload
            class="video-uploader"
            :action="uploadVideoUrl"
            :headers="uploadHeaders"
            :show-file-list="true"
            :on-success="handleVideoSuccess"
            :before-upload="beforeVideoUpload"
            accept="video/*"
            :limit="1"
          >
            <el-button type="primary">
              <el-icon><VideoCamera /></el-icon>
              上传视频
            </el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持 MP4, WebM 等视频格式
              </div>
            </template>
          </el-upload>
          <el-input
            v-if="form.previewVideoUrl"
            v-model="form.previewVideoUrl"
            placeholder="或手动输入视频URL"
            style="margin-top: 10px"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="2">内测</el-radio>
            <el-radio :label="1">上架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, VideoCamera } from '@element-plus/icons-vue'
import { getToken } from '@/utils/auth'
import {
  getWorkflowList,
  searchWorkflows,
  getWorkflowById,
  createWorkflow,
  updateWorkflow,
  deleteWorkflow,
  publishWorkflow,
  unpublishWorkflow,
  batchPublish,
  batchUnpublish,
  executeWorkflow,
  getWorkflowParameters
} from '@/api/workflow'

const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const searchKeyword = ref('')
const selectedIds = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const submitting = ref(false)
const form = ref({
  name: '',
  description: '',
  category: '',
  pointsCost: 0,
  coverUrl: '',
  previewVideoUrl: '',
  cozeWorkflowId: '',
  status: 2
})

const rules = {
  name: [{ required: true, message: '请输入工作流名称', trigger: 'blur' }],
  cozeWorkflowId: [{ required: true, message: '请输入Coze工作流ID', trigger: 'blur' }],
  category: [{ required: true, message: '请输入分类', trigger: 'blur' }]
}

// 执行对话框
const executeDialogVisible = ref(false)
const executeFormRef = ref(null)
const executeSubmitting = ref(false)
const executeForm = ref({
  workflowId: '',
  workflowName: '',
  cozeWorkflowId: '',
  parameterDefinition: [],
  parameters: {}
})

// 上传配置
const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'
const uploadImageUrl = `${baseURL}/admin/v1/files/upload/image`
const uploadVideoUrl = `${baseURL}/admin/v1/files/upload/video`

// 通用文件上传URL（用于音频等其他类型）
const uploadFileUrl = `${baseURL}/admin/v1/files/upload/image`
const uploadHeaders = {
  Authorization: `Bearer ${getToken()}`
}

// 封面上传成功处理
const handleCoverSuccess = (response) => {
  form.value.coverUrl = response.data.url
  ElMessage.success('封面上传成功')
}

// 视频上传成功处理
const handleVideoSuccess = (response) => {
  form.value.previewVideoUrl = response.data.url
  ElMessage.success('视频上传成功')
}

// 图片上传前验证
const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

// 视频上传前验证
const beforeVideoUpload = (file) => {
  const isVideo = file.type.startsWith('video/')
  const isLt100M = file.size / 1024 / 1024 < 100

  if (!isVideo) {
    ElMessage.error('只能上传视频文件!')
    return false
  }
  if (!isLt100M) {
    ElMessage.error('视频大小不能超过 100MB!')
    return false
  }
  return true
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    }
    const res = searchKeyword.value
      ? await searchWorkflows(searchKeyword.value, params)
      : await getWorkflowList(params)
    tableData.value = res.content || []
    total.value = res.totalElements || 0
  } catch (error) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

const handleCreate = () => {
  dialogTitle.value = '新增工作流'
  form.value = {
    name: '',
    description: '',
    category: '',
    pointsCost: 0,
    coverUrl: '',
    previewVideoUrl: '',
    cozeWorkflowId: '',
    status: 2
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑工作流'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (form.value.id) {
          await updateWorkflow(form.value.id, form.value)
          ElMessage.success('更新成功')
        } else {
          await createWorkflow(form.value)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

const handleToggleStatus = async (row) => {
  try {
    if (row.status === 1) {
      await unpublishWorkflow(row.id)
      ElMessage.success('下架成功')
    } else {
      await publishWorkflow(row.id)
      ElMessage.success('上架成功')
    }
    fetchData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该工作流吗？', '提示', {
      type: 'warning'
    })
    await deleteWorkflow(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleBatchPublish = async () => {
  try {
    await batchPublish(selectedIds.value)
    ElMessage.success('批量上架成功')
    fetchData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleBatchUnpublish = async () => {
  try {
    await batchUnpublish(selectedIds.value)
    ElMessage.success('批量下架成功')
    fetchData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  fetchData()
})

// 解析参数定义（处理字符串或数组格式）
const parseParameterDefinition = (paramDef) => {
  if (!paramDef) return []

  // 如果是数组，直接使用
  if (Array.isArray(paramDef)) {
    return paramDef
  }

  // 如果是字符串，尝试解析 JSON
  if (typeof paramDef === 'string') {
    try {
      const parsed = JSON.parse(paramDef)
      // 解析后是数组则返回，否则返回空数组
      return Array.isArray(parsed) ? parsed : []
    } catch (e) {
      console.warn('参数定义解析失败:', paramDef)
      return []
    }
  }

  return []
}

// 打开执行对话框
const handleExecute = async (row) => {
  try {
    // 从 Coze 平台获取工作流参数定义
    let parameterDefinition = []
    try {
      parameterDefinition = await getWorkflowParameters(row.id)
    } catch (e) {
      console.warn('从Coze获取参数定义失败，尝试本地参数:', e)
      // 如果Coze接口失败，尝试本地参数定义
      const workflow = await getWorkflowById(row.id)
      parameterDefinition = parseParameterDefinition(workflow.parameterDefinition)
    }

    executeForm.value = {
      workflowId: row.id,
      workflowName: row.name,
      cozeWorkflowId: row.cozeWorkflowId,
      parameterDefinition: parameterDefinition || [],
      parameters: {}
    }

    // 根据参数定义初始化参数值
    if (executeForm.value.parameterDefinition && executeForm.value.parameterDefinition.length > 0) {
      executeForm.value.parameterDefinition.forEach(param => {
        // mihe_key 参数初始化为空字符串，由后端自动填充
        if (param.key === 'mihe_key') {
          executeForm.value.parameters[param.key] = ''
        } else if (param.default !== undefined && param.default !== null) {
          executeForm.value.parameters[param.key] = param.default
        } else if (param.type === 'boolean') {
          executeForm.value.parameters[param.key] = false
        } else if (param.type === 'multi_select') {
          executeForm.value.parameters[param.key] = []
        } else {
          executeForm.value.parameters[param.key] = ''
        }
      })
    }

    executeDialogVisible.value = true
  } catch (error) {
    console.error('执行对话框打开失败:', error)
    ElMessage.error('获取工作流参数定义失败: ' + (error.message || '未知错误'))
  }
}

// 参数文件上传成功处理
const handleParamFileSuccess = (paramKey, response) => {
  executeForm.value.parameters[paramKey] = response.data.url
  ElMessage.success('上传成功')
}

// 提交执行
const handleExecuteSubmit = async () => {
  // 验证必填参数（排除 mihe_key，因为它由后端自动填充）
  const missingParams = []
  if (executeForm.value.parameterDefinition) {
    executeForm.value.parameterDefinition.forEach(param => {
      // 跳过 mihe_key 的验证，因为它由后端自动填充
      if (param.key === 'mihe_key') {
        return
      }

      if (param.required) {
        const value = executeForm.value.parameters[param.key]
        if (value === undefined || value === null || value === '' ||
            (Array.isArray(value) && value.length === 0)) {
          missingParams.push(param.name)
        }
      }
    })
  }

  if (missingParams.length > 0) {
    ElMessage.error(`请填写必填参数: ${missingParams.join(', ')}`)
    return
  }

  executeSubmitting.value = true
  try {
    // 提交参数，mihe_key 会作为空字符串提交，由后端自动填充
    const result = await executeWorkflow(
      executeForm.value.workflowId,
      executeForm.value.parameters
    )
    ElMessage.success(`任务已创建: ${result.id}`)
    executeDialogVisible.value = false
  } catch (error) {
    ElMessage.error(error.message || '执行失败')
  } finally {
    executeSubmitting.value = false
  }
}
</script>

<style scoped>
.workflow-list h2 {
  margin-bottom: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cover-uploader {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  width: 178px;
  height: 178px;
}

.cover-uploader:hover {
  border-color: var(--el-color-primary);
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-image {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
}

.video-uploader {
  display: flex;
  flex-direction: column;
}

.el-upload__tip {
  margin-top: 8px;
  color: #909399;
}

.static-info :deep(.el-form-item__label) {
  color: #606266;
  font-weight: 500;
}

.static-info span {
  color: #303133;
  font-weight: 500;
}

.param-description {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.file-name {
  color: #67c23a;
  font-size: 13px;
  line-height: 1.4;
}

.upload-wrapper {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.upload-demo {
  display: flex;
  align-items: center;
}

.file-hint {
  color: #909399;
  font-size: 13px;
}

.file-desc {
  color: #909399;
  font-size: 13px;
  line-height: 1.4;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  line-height: 1.4;
  padding-right: 8px;
}

:deep(.el-form-item) {
  margin-bottom: 18px;
}

:deep(.el-form-item__content) {
  line-height: 32px;
}

.number-input-wrapper {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.param-hint {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}
</style>
