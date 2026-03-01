import request from './request'

export const getTaskList = (params) => {
  return request({
    url: '/admin/v1/tasks',
    method: 'get',
    params
  })
}

export const getTaskById = (id) => {
  return request({
    url: `/admin/v1/tasks/${id}`,
    method: 'get'
  })
}

export const cancelTask = (id) => {
  return request({
    url: `/admin/v1/tasks/${id}/cancel`,
    method: 'put'
  })
}

export const batchCancel = (ids) => {
  return request({
    url: '/admin/v1/tasks/batch/cancel',
    method: 'put',
    data: ids
  })
}

export const getProcessingTasks = () => {
  return request({
    url: '/admin/v1/tasks/processing',
    method: 'get'
  })
}

export const getQueuedTasks = () => {
  return request({
    url: '/admin/v1/tasks/queued',
    method: 'get'
  })
}

export const getTaskStats = (status) => {
  return request({
    url: '/admin/v1/tasks/stats/count',
    method: 'get',
    params: { status }
  })
}
