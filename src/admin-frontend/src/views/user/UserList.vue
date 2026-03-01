<template>
  <div class="user-list">
    <h2>用户管理</h2>
    <el-card>
      <div class="toolbar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索用户昵称"
          style="width: 300px"
          clearable
          @clear="handleSearch"
        >
          <template #append>
            <el-button icon="Search" @click="handleSearch" />
          </template>
        </el-input>
        <div>
          <el-button
            type="danger"
            icon="Lock"
            :disabled="!selectedIds.length"
            @click="handleBatchDisable"
          >
            批量禁用
          </el-button>
          <el-button
            type="success"
            icon="Unlock"
            :disabled="!selectedIds.length"
            @click="handleBatchEnable"
          >
            批量启用
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
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :size="40" :src="row.avatarUrl" />
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="pointsBalance" label="资源点" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              :type="row.status === 1 ? 'danger' : 'success'"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button link type="primary" @click="handleAdjustPoints(row)">
              调整资源点
            </el-button>
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

    <!-- 调整资源点对话框 -->
    <el-dialog v-model="pointsDialogVisible" title="调整资源点" width="400px">
      <el-form :model="pointsForm" ref="pointsFormRef" label-width="100px">
        <el-form-item label="当前用户">
          <span>{{ currentUser?.nickname }}</span>
        </el-form-item>
        <el-form-item label="当前余额">
          <span>{{ currentUser?.pointsBalance }}</span>
        </el-form-item>
        <el-form-item
          label="调整数量"
          prop="points"
          :rules="[{ required: true, message: '请输入调整数量', trigger: 'blur' }]"
        >
          <el-input-number v-model="pointsForm.points" :min="-10000" :max="10000" />
          <div class="form-tip">正数为增加，负数为减少</div>
        </el-form-item>
        <el-form-item label="调整原因">
          <el-input v-model="pointsForm.reason" type="textarea" :rows="2" placeholder="请输入调整原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pointsDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitPoints" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, searchUsers, disableUser, enableUser, adjustPoints, batchDisable, batchEnable } from '@/api/user'

const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const searchKeyword = ref('')
const selectedIds = ref([])

const pointsDialogVisible = ref(false)
const pointsFormRef = ref(null)
const submitting = ref(false)
const currentUser = ref(null)
const pointsForm = ref({
  points: 0,
  reason: ''
})

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString()
}

const fetchData = async () => {
  loading.value = true
  try {
    let res
    if (searchKeyword.value) {
      res = await searchUsers(searchKeyword.value)
      tableData.value = res || []
      total.value = res?.length || 0
    } else {
      res = await getUserList({ page: currentPage.value - 1, size: pageSize.value })
      tableData.value = res?.content || []
      total.value = res?.totalElements || 0
    }
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

const handleToggleStatus = async (row) => {
  try {
    if (row.status === 1) {
      await ElMessageBox.confirm('确定要禁用该用户吗？', '提示', { type: 'warning' })
      await disableUser(row.id)
      ElMessage.success('禁用成功')
    } else {
      await enableUser(row.id)
      ElMessage.success('启用成功')
    }
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleAdjustPoints = (row) => {
  currentUser.value = row
  pointsForm.value = { points: 0, reason: '' }
  pointsDialogVisible.value = true
}

const handleSubmitPoints = async () => {
  if (!pointsFormRef.value) return
  await pointsFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        await adjustPoints(currentUser.value.id, pointsForm.value.points, pointsForm.value.reason)
        ElMessage.success('调整成功')
        pointsDialogVisible.value = false
        fetchData()
      } catch (error) {
        ElMessage.error(error.message || '调整失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

const handleBatchDisable = async () => {
  try {
    await ElMessageBox.confirm(`确定要禁用选中的 ${selectedIds.value.length} 个用户吗？`, '提示', { type: 'warning' })
    await batchDisable(selectedIds.value)
    ElMessage.success('批量禁用成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleBatchEnable = async () => {
  try {
    await batchEnable(selectedIds.value)
    ElMessage.success('批量启用成功')
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
.user-list h2 {
  margin-bottom: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-tip {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}
</style>
