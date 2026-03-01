<template>
  <div class="sidebar">
    <div class="logo">
      <h3>管理后台</h3>
    </div>
    <el-menu
      :default-active="activeMenu"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409EFF"
      router
    >
      <el-menu-item index="/dashboard">
        <el-icon><DataLine /></el-icon>
        <span>仪表盘</span>
      </el-menu-item>
      <el-menu-item index="/workflows">
        <el-icon><Grid /></el-icon>
        <span>工作流管理</span>
      </el-menu-item>
      <el-menu-item index="/users">
        <el-icon><User /></el-icon>
        <span>用户管理</span>
      </el-menu-item>
      <el-menu-item index="/tasks">
        <el-icon><List /></el-icon>
        <span>任务监控</span>
      </el-menu-item>
      <el-sub-menu index="system">
        <template #title>
          <el-icon><Setting /></el-icon>
          <span>系统管理</span>
        </template>
        <el-menu-item index="/system/admins">管理员</el-menu-item>
        <el-menu-item index="/system/roles">角色权限</el-menu-item>
      </el-sub-menu>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style scoped>
.sidebar {
  height: 100%;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b3a4b;
}

.logo h3 {
  color: #fff;
  margin: 0;
}
</style>
