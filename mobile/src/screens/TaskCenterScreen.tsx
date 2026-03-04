import React from 'react';
import { View, StyleSheet, FlatList, RefreshControl, TouchableOpacity, ScrollView } from 'react-native';
import { Text, Card, Button } from 'react-native-paper';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useNavigation } from '@react-navigation/native';
import { StackNavigationProp } from '@react-navigation/stack';
import { RootStackParamList } from '../navigation/AppNavigator';
import { COLORS, TASK_STATUS, TASK_STATUS_LABEL } from '../utils/constants';

type TaskCenterScreenNavigationProp = StackNavigationProp<RootStackParamList, 'Main'>;

const TaskCenterScreen = () => {
  const navigation = useNavigation<TaskCenterScreenNavigationProp>();
  const [selectedStatus, setSelectedStatus] = React.useState('all');
  const [refreshing, setRefreshing] = React.useState(false);

  const onRefresh = React.useCallback(() => {
    setRefreshing(true);
    // 模拟网络请求
    setTimeout(() => {
      setRefreshing(false);
    }, 1000);
  }, []);

  // 模拟任务数据
  const tasks = [
    {
      id: 'task_001',
      workflowName: 'AI视频人物换脸',
      status: TASK_STATUS.PROCESSING,
      progress: 65,
      estimatedPoints: 1500,
      createdAt: '2026-03-03 10:00:00',
    },
    {
      id: 'task_002',
      workflowName: 'AI图片风格转换',
      status: TASK_STATUS.COMPLETED,
      progress: 100,
      estimatedPoints: 500,
      actualPoints: 480,
      createdAt: '2026-03-03 09:30:00',
    },
    {
      id: 'task_003',
      workflowName: 'AI文本生成',
      status: TASK_STATUS.FAILED,
      progress: 0,
      estimatedPoints: 200,
      createdAt: '2026-03-03 09:00:00',
    },
  ];

  const filteredTasks = tasks.filter(task => {
    if (selectedStatus !== 'all' && task.status !== selectedStatus) {
      return false;
    }
    return true;
  });

  const getStatusColor = (status: string) => {
    switch (status) {
      case TASK_STATUS.PROCESSING:
        return COLORS.warning;
      case TASK_STATUS.COMPLETED:
        return COLORS.success;
      case TASK_STATUS.FAILED:
        return COLORS.error;
      default:
        return COLORS.textSecondary;
    }
  };

  const getStatusBackgroundColor = (status: string) => {
    switch (status) {
      case TASK_STATUS.PROCESSING:
        return 'rgba(255, 193, 7, 0.1)';
      case TASK_STATUS.COMPLETED:
        return 'rgba(40, 167, 69, 0.1)';
      case TASK_STATUS.FAILED:
        return 'rgba(220, 53, 69, 0.1)';
      default:
        return 'rgba(108, 117, 125, 0.1)';
    }
  };

  const renderTaskItem = ({ item }: { item: any }) => (
    <Card style={styles.taskCard} onPress={() => navigation.navigate('TaskDetail', { taskId: item.id })}>
      <Card.Content>
        <View style={styles.taskHeader}>
          <Text style={styles.workflowName}>{item.workflowName}</Text>
          <Text style={[styles.statusText, { color: getStatusColor(item.status), backgroundColor: getStatusBackgroundColor(item.status) }]}>
            {TASK_STATUS_LABEL[item.status as keyof typeof TASK_STATUS_LABEL]}
          </Text>
        </View>
        <View style={styles.progressContainer}>
          <View style={styles.progressBar}>
            <View style={[styles.progressFill, { width: `${item.progress}%` }]} />
          </View>
          <Text style={styles.progressText}>{item.progress}%</Text>
        </View>
        <View style={styles.taskInfo}>
          <Text style={styles.infoText}>消耗: {item.actualPoints || item.estimatedPoints} 资源点</Text>
          <Text style={styles.infoText}>提交时间: {item.createdAt}</Text>
        </View>
      </Card.Content>
      <Card.Actions>
        <Button onPress={() => navigation.navigate('TaskDetail', { taskId: item.id })}>
          查看详情
        </Button>
        {item.status === TASK_STATUS.FAILED && (
          <Button mode="contained" style={styles.retryButton}>
            重新运行
          </Button>
        )}
      </Card.Actions>
    </Card>
  );

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      <View style={styles.header}>
        <ScrollView
          horizontal
          showsHorizontalScrollIndicator={false}
          contentContainerStyle={styles.tabContent}
        >
          <TouchableOpacity
            style={[styles.tabItem, selectedStatus === 'all' && styles.tabItemActive]}
            onPress={() => setSelectedStatus('all')}
          >
            <Text style={[styles.tabText, selectedStatus === 'all' && styles.tabTextActive]}>
              全部
            </Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.tabItem, selectedStatus === TASK_STATUS.PROCESSING && styles.tabItemActive]}
            onPress={() => setSelectedStatus(TASK_STATUS.PROCESSING)}
          >
            <Text style={[styles.tabText, selectedStatus === TASK_STATUS.PROCESSING && styles.tabTextActive]}>
              处理中
            </Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.tabItem, selectedStatus === TASK_STATUS.COMPLETED && styles.tabItemActive]}
            onPress={() => setSelectedStatus(TASK_STATUS.COMPLETED)}
          >
            <Text style={[styles.tabText, selectedStatus === TASK_STATUS.COMPLETED && styles.tabTextActive]}>
              已完成
            </Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.tabItem, selectedStatus === TASK_STATUS.FAILED && styles.tabItemActive]}
            onPress={() => setSelectedStatus(TASK_STATUS.FAILED)}
          >
            <Text style={[styles.tabText, selectedStatus === TASK_STATUS.FAILED && styles.tabTextActive]}>
              失败
            </Text>
          </TouchableOpacity>
        </ScrollView>
      </View>

      <FlatList
        data={filteredTasks}
        renderItem={renderTaskItem}
        keyExtractor={(item) => item.id}
        contentContainerStyle={styles.listContent}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
        }
        ListEmptyComponent={
          <View style={styles.emptyContainer}>
            <Text style={styles.emptyText}>暂无任务</Text>
          </View>
        }
      />
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#0F0F10',
  },
  header: {
    backgroundColor: '#0F0F10',
    paddingTop: 8,
    paddingBottom: 12,
  },
  tabContent: {
    paddingHorizontal: 16,
    alignItems: 'center',
    gap: 4,
  },
  tabItem: {
    paddingHorizontal: 12,
    paddingVertical: 8,
    marginRight: 4,
    backgroundColor: 'transparent',
    height: 36,
    justifyContent: 'center',
  },
  tabItemActive: {
    backgroundColor: 'transparent',
  },
  tabText: {
    fontSize: 14,
    color: COLORS.textSecondary,
    fontWeight: '500',
  },
  tabTextActive: {
    fontSize: 16,
    color: '#FFFFFF',
    fontWeight: '700',
  },
  listContent: {
    padding: 16,
    paddingTop: 8,
    paddingBottom: 20,
  },
  taskCard: {
    marginBottom: 16,
    backgroundColor: COLORS.surface,
    elevation: 3,
    borderRadius: 12,
  },
  taskHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 16,
  },
  workflowName: {
    fontSize: 16,
    fontWeight: 'bold',
    color: COLORS.text,
  },
  statusText: {
    fontSize: 14,
    fontWeight: '600',
    paddingHorizontal: 12,
    paddingVertical: 4,
    borderRadius: 12,
  },
  progressContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 16,
  },
  progressBar: {
    flex: 1,
    height: 10,
    backgroundColor: COLORS.border,
    borderRadius: 5,
    marginRight: 12,
    overflow: 'hidden',
  },
  progressFill: {
    height: '100%',
    backgroundColor: COLORS.primary,
    borderRadius: 5,
  },
  progressText: {
    fontSize: 14,
    color: COLORS.textSecondary,
    minWidth: 45,
    fontWeight: '500',
  },
  taskInfo: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    flexWrap: 'wrap',
    gap: 8,
  },
  infoText: {
    fontSize: 13,
    color: COLORS.textSecondary,
  },
  retryButton: {
    marginLeft: 'auto',
    borderRadius: 8,
    paddingHorizontal: 16,
  },
  emptyContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingVertical: 120,
  },
  emptyText: {
    fontSize: 16,
    color: COLORS.textSecondary,
  },
});

export default TaskCenterScreen;