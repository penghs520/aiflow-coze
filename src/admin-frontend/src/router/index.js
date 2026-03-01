import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue')
  },
  {
    path: '/',
    component: () => import('@/components/layout/AppLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue')
      },
      {
        path: 'workflows',
        name: 'Workflows',
        component: () => import('@/views/workflow/WorkflowList.vue')
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/user/UserList.vue')
      },
      {
        path: 'tasks',
        name: 'Tasks',
        component: () => import('@/views/task/TaskList.vue')
      },
      {
        path: 'system/admins',
        name: 'Admins',
        component: () => import('@/views/system/AdminList.vue')
      },
      {
        path: 'system/roles',
        name: 'Roles',
        component: () => import('@/views/system/RoleList.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = getToken()
  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else {
    next()
  }
})

export default router
