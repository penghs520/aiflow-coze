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
  createWorkflow,
  updateWorkflow,
  deleteWorkflow,
  publishWorkflow,
  unpublishWorkflow,
  batchPublish,
  batchUnpublish
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

// 上传配置
const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'
const uploadImageUrl = `${baseURL}/admin/v1/files/upload/image`
const uploadVideoUrl = `${baseURL}/admin/v1/files/upload/video`
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
</style>
