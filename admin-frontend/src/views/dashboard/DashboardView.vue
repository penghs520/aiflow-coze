<template>
  <div class="dashboard">
    <h2>仪表盘</h2>
    <el-row :gutter="20" v-loading="loading">
      <el-col :span="6">
        <el-card>
          <div class="stat-card">
            <div class="stat-icon user">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ data.totalUsers || 0 }}</div>
              <div class="stat-label">总用户数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-card">
            <div class="stat-icon task">
              <el-icon><List /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ data.totalTasks || 0 }}</div>
              <div class="stat-label">总任务数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-card">
            <div class="stat-icon workflow">
              <el-icon><Grid /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ data.totalWorkflows || 0 }}</div>
              <div class="stat-label">工作流数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-card">
            <div class="stat-icon running">
              <el-icon><Loading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ data.runningTasks || 0 }}</div>
              <div class="stat-label">运行中任务</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getDashboardData } from '@/api/dashboard'

const loading = ref(false)
const data = ref({})

onMounted(async () => {
  loading.value = true
  try {
    data.value = await getDashboardData()
  } catch (error) {
    console.error('获取仪表盘数据失败', error)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.dashboard h2 {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
}

.stat-icon.user { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.stat-icon.task { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.stat-icon.workflow { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.stat-icon.running { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }

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
</style>
