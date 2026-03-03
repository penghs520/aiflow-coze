<template>
  <div class="task-list">
    <h2>任务监控</h2>
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="6">
        <el-card>
          <div class="stat-item">
            <div class="stat-value">{{ stats.total || 0 }}</div>
            <div class="stat-label">总任务数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-item">
            <div class="stat-value" style="color: #67c23a">{{ stats.processing || 0 }}</div>
            <div class="stat-label">处理中</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-item">
            <div class="stat-value" style="color: #e6a23c">{{ stats.queued || 0 }}</div>
            <div class="stat-label">排队中</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-item">
            <div class="stat-value" style="color: #909399">{{ stats.today || 0 }}</div>
            <div class="stat-label">今日任务</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <div class="toolbar">
        <div>
          <el-radio-group v-model="filterStatus" @change="handleFilterChange">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="0">排队中</el-radio-button>
            <el-radio-button label="1">处理中</el-radio-button>
            <el-radio-button label="2">已完成</el-radio-button>
            <el-radio-button label="3">失败</el-radio-button>
            <el-radio-button label="4">已取消</el-radio-button>
          </el-radio-group>
        </div>
        <el-button
          type="danger"
          icon="CircleClose"
          :disabled="!selectedIds.length"
          @click="handleBatchCancel"
        >
          批量取消
        </el-button>
      </div>

      <el-table
        :data="tableData"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        style="margin-top: 20px"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="任务ID" width="280" />
        <el-table-column prop="workflowName" label="工作流" min-width="150" />
        <el-table-column prop="userNickname" label="用户" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="pointsCost" label="消耗资源点" width="100" />
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="canCancel(row.status)"
              link
              type="danger"
              @click="handleCancel(row)"
            >
              取消
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
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import { getTaskList, cancelTask, batchCancel, getTaskStats, getProcessingTasks, getQueuedTasks } from '@/api/task'
import websocketService from '@/utils/websocket'

const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const filterStatus = ref('')
const selectedIds = ref([])
const stats = ref({})

// WebSocket 订阅引用
let taskNotificationSubscription = null

/**
 * 处理任务完成通知
 */
const handleTaskNotification = (notification) => {
  console.log('收到任务通知:', notification)

  // 根据任务状态显示不同的通知
  if (notification.type === 'TASK_COMPLETED') {
    ElNotification({
      title: '任务完成',
      message: `工作流「${notification.workflowName}」执行完成\n用户：${notification.userNickname}`,
      type: 'success',
      duration: 5000,
      position: 'top-right'
    })
  } else if (notification.type === 'TASK_FAILED') {
    ElNotification({
      title: '任务失败',
      message: `工作流「${notification.workflowName}」执行失败\n用户：${notification.userNickname}\n原因：${notification.errorMessage || '未知错误'}`,
      type: 'error',
      duration: 8000,
      position: 'top-right'
    })
  }

  // 刷新任务列表和统计数据
  fetchData()
  fetchStats()
}

/**
 * 初始化 WebSocket 连接
 */
const initWebSocket = async () => {
  try {
    await websocketService.connect()

    // 订阅任务通知主题
    taskNotificationSubscription = websocketService.subscribe(
      '/topic/task-notifications',
      handleTaskNotification
    )

    console.log('WebSocket 连接并订阅成功')
  } catch (error) {
    console.error('WebSocket 连接失败:', error)
    // 不显示错误提示，因为用户可能不需要知道 WebSocket 连接失败
  }
}

/**
 * 清理 WebSocket 连接
 */
const cleanupWebSocket = () => {
  if (taskNotificationSubscription) {
    websocketService.unsubscribe('/topic/task-notifications')
    taskNotificationSubscription = null
  }
  // 注意：不要在这里 disconnect，因为可能有其他页面也在使用
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString()
}

const getStatusType = (status) => {
  const map = {
    0: 'info',      // 排队中
    1: 'warning',   // 处理中
    2: 'success',   // 已完成
    3: 'danger',    // 失败
    4: ''           // 已取消
  }
  return map[status] || ''
}

const getStatusText = (status) => {
  const map = {
    0: '排队中',
    1: '处理中',
    2: '已完成',
    3: '失败',
    4: '已取消'
  }
  return map[status] || '未知'
}

const canCancel = (status) => {
  return status === 0 || status === 1  // 只有排队中或处理中的任务可以取消
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    }
    if (filterStatus.value !== '') {
      params.status = parseInt(filterStatus.value)
    }
    const res = await getTaskList(params)
    tableData.value = res?.content || []
    total.value = res?.totalElements || 0
  } catch (error) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const fetchStats = async () => {
  try {
    const [totalRes, processingRes, queuedRes, todayRes] = await Promise.all([
      getTaskStats(),
      getTaskStats(1),
      getTaskStats(0),
      getTaskStats()
    ])
    stats.value = {
      total: totalRes?.total || 0,
      processing: processingRes?.count || 0,
      queued: queuedRes?.count || 0,
      today: todayRes?.today || 0
    }
  } catch (error) {
    console.error('获取统计失败', error)
  }
}

const handleFilterChange = () => {
  currentPage.value = 1
  fetchData()
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消该任务吗？', '提示', { type: 'warning' })
    await cancelTask(row.id)
    ElMessage.success('取消成功')
    fetchData()
    fetchStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

const handleBatchCancel = async () => {
  try {
    await ElMessageBox.confirm(`确定要取消选中的 ${selectedIds.value.length} 个任务吗？`, '提示', { type: 'warning' })
    await batchCancel(selectedIds.value)
    ElMessage.success('批量取消成功')
    fetchData()
    fetchStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

onMounted(() => {
  fetchData()
  fetchStats()
  initWebSocket()  // 初始化 WebSocket
})

onUnmounted(() => {
  cleanupWebSocket()  // 清理 WebSocket 订阅
})
</script>

<style scoped>
.task-list h2 {
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 5px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
