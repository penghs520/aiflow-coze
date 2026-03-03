import React, { useEffect, useState } from 'react';
import { View, StyleSheet, ScrollView, Image } from 'react-native';
import { Text, Card, Button, Divider, ProgressBar } from 'react-native-paper';
import { useRoute, useNavigation, RouteProp } from '@react-navigation/native';
import { StackNavigationProp } from '@react-navigation/stack';
import { VideoView, useVideoPlayer } from 'expo-video';
import { RootStackParamList } from '../navigation/AppNavigator';
import { COLORS, TASK_STATUS, TASK_STATUS_LABEL } from '../utils/constants';

type TaskDetailScreenRouteProp = RouteProp<RootStackParamList, 'TaskDetail'>;
type TaskDetailScreenNavigationProp = StackNavigationProp<RootStackParamList, 'TaskDetail'>;

const TaskDetailScreen = () => {
  const route = useRoute<TaskDetailScreenRouteProp>();
  const navigation = useNavigation<TaskDetailScreenNavigationProp>();
  const { taskId } = route.params;
  const [task, setTask] = useState<any>(null);
  const [videoUrl, setVideoUrl] = useState<string>('');

  const player = useVideoPlayer(videoUrl, player => {
    if (videoUrl) {
      player.loop = false;
    }
  });

  useEffect(() => {
    // 模拟获取任务详情
    setTimeout(() => {
      setTask({
        id: taskId,
        workflowName: 'AI视频人物换脸',
        status: TASK_STATUS.PROCESSING,
        progress: 65,
        estimatedPoints: 1500,
        actualPoints: null,
        parameters: {
          source_video: 'video.mp4',
          target_face_image: 'face.jpg',
          output_resolution: '1080p'
        },
        result: null,
        errorMessage: null,
        timestamps: {
          createdAt: '2026-03-03 10:00:00',
          startedAt: '2026-03-03 10:00:05',
          completedAt: null
        },
        logs: [
          {
            timestamp: '2026-03-03 10:00:00',
            level: 'info',
            message: '任务提交成功'
          },
          {
            timestamp: '2026-03-03 10:00:05',
            level: 'info',
            message: '任务开始处理'
          },
          {
            timestamp: '2026-03-03 10:02:30',
            level: 'info',
            message: '正在处理视频...'
          }
        ]
      });
    }, 500);
  }, [taskId]);

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

  if (!task) {
    return (
      <View style={styles.loadingContainer}>
        <Text style={styles.loadingText}>加载中...</Text>
      </View>
    );
  }

  return (
    <ScrollView style={styles.container}>
      <Card style={styles.statusCard}>
        <Card.Content>
          <View style={styles.statusHeader}>
            <Text style={styles.workflowName}>{task.workflowName}</Text>
            <Text style={[styles.statusText, { color: getStatusColor(task.status) }]}>
              {TASK_STATUS_LABEL[task.status as keyof typeof TASK_STATUS_LABEL]}
            </Text>
          </View>
          <ProgressBar
            progress={task.progress / 100}
            color={getStatusColor(task.status)}
            style={styles.progressBar}
          />
          <Text style={styles.progressText}>{task.progress}%</Text>
        </Card.Content>
      </Card>

      <Card style={styles.infoCard}>
        <Card.Content>
          <Text style={styles.sectionTitle}>任务信息</Text>
          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>提交时间</Text>
            <Text style={styles.infoValue}>{task.timestamps.createdAt}</Text>
          </View>
          {task.timestamps.startedAt && (
            <View style={styles.infoRow}>
              <Text style={styles.infoLabel}>开始时间</Text>
              <Text style={styles.infoValue}>{task.timestamps.startedAt}</Text>
            </View>
          )}
          {task.timestamps.completedAt && (
            <View style={styles.infoRow}>
              <Text style={styles.infoLabel}>完成时间</Text>
              <Text style={styles.infoValue}>{task.timestamps.completedAt}</Text>
            </View>
          )}
          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>消耗资源点</Text>
            <Text style={styles.infoValue}>
              {task.actualPoints || task.estimatedPoints} 点
            </Text>
          </View>
        </Card.Content>
      </Card>

      <Card style={styles.paramsCard}>
        <Card.Content>
          <Text style={styles.sectionTitle}>输入参数</Text>
          {Object.entries(task.parameters).map(([key, value], index) => (
            <View key={index} style={styles.paramRow}>
              <Text style={styles.paramKey}>{key}</Text>
              <Text style={styles.paramValue}>{String(value)}</Text>
            </View>
          ))}
        </Card.Content>
      </Card>

      {task.result && (
        <Card style={styles.resultCard}>
          <Card.Content>
            <Text style={styles.sectionTitle}>执行结果</Text>
            {task.result.outputVideoUrl && (
              <>
                <VideoView
                  player={player}
                  style={styles.resultVideo}
                  nativeControls
                  contentFit="contain"
                />
                <Button mode="contained" style={styles.downloadButton}>
                  下载视频
                </Button>
              </>
            )}
            {task.result.thumbnailUrl && !task.result.outputVideoUrl && (
              <Image source={{ uri: task.result.thumbnailUrl }} style={styles.resultImage} />
            )}
            {task.result.outputImageUrl && (
              <Button mode="contained" style={styles.downloadButton}>
                下载图片
              </Button>
            )}
          </Card.Content>
        </Card>
      )}

      {task.errorMessage && (
        <Card style={styles.errorCard}>
          <Card.Content>
            <Text style={styles.sectionTitle}>错误信息</Text>
            <Text style={styles.errorMessage}>{task.errorMessage}</Text>
          </Card.Content>
        </Card>
      )}

      <Card style={styles.logsCard}>
        <Card.Content>
          <Text style={styles.sectionTitle}>执行日志</Text>
          {task.logs.map((log: any, index: number) => (
            <View key={index} style={styles.logItem}>
              <Text style={styles.logTimestamp}>{log.timestamp}</Text>
              <Text style={styles.logMessage}>{log.message}</Text>
            </View>
          ))}
        </Card.Content>
      </Card>

      <View style={styles.actionsContainer}>
        {task.status === TASK_STATUS.PROCESSING && (
          <Button mode="outlined" style={styles.actionButton}>
            取消任务
          </Button>
        )}
        {task.status === TASK_STATUS.FAILED && (
          <Button mode="contained" style={styles.actionButton}>
            重新运行
          </Button>
        )}
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: COLORS.background,
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  loadingText: {
    fontSize: 16,
    color: COLORS.textSecondary,
  },
  statusCard: {
    margin: 16,
    backgroundColor: COLORS.surface,
    elevation: 2,
  },
  statusHeader: {
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
    fontWeight: '500',
  },
  progressBar: {
    height: 8,
    borderRadius: 4,
  },
  progressText: {
    fontSize: 14,
    color: COLORS.textSecondary,
    marginTop: 8,
    textAlign: 'center',
  },
  infoCard: {
    margin: 16,
    marginTop: 0,
    backgroundColor: COLORS.surface,
    elevation: 2,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: COLORS.text,
    marginBottom: 12,
  },
  infoRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 8,
  },
  infoLabel: {
    fontSize: 14,
    color: COLORS.textSecondary,
  },
  infoValue: {
    fontSize: 14,
    color: COLORS.text,
  },
  paramsCard: {
    margin: 16,
    marginTop: 0,
    backgroundColor: COLORS.surface,
    elevation: 2,
  },
  paramRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 8,
  },
  paramKey: {
    fontSize: 14,
    color: COLORS.textSecondary,
  },
  paramValue: {
    fontSize: 14,
    color: COLORS.text,
  },
  resultCard: {
    margin: 16,
    marginTop: 0,
    backgroundColor: COLORS.surface,
    elevation: 2,
  },
  resultImage: {
    width: '100%',
    height: 200,
    borderRadius: 8,
    marginBottom: 16,
  },
  resultVideo: {
    width: '100%',
    height: 200,
    borderRadius: 8,
    marginBottom: 16,
  },
  downloadButton: {
    marginBottom: 8,
  },
  errorCard: {
    margin: 16,
    marginTop: 0,
    backgroundColor: COLORS.surface,
    elevation: 2,
  },
  errorMessage: {
    fontSize: 14,
    color: COLORS.error,
    lineHeight: 20,
  },
  logsCard: {
    margin: 16,
    marginTop: 0,
    backgroundColor: COLORS.surface,
    elevation: 2,
  },
  logItem: {
    marginBottom: 8,
  },
  logTimestamp: {
    fontSize: 12,
    color: COLORS.textSecondary,
    marginBottom: 4,
  },
  logMessage: {
    fontSize: 14,
    color: COLORS.text,
  },
  actionsContainer: {
    padding: 16,
    marginBottom: 20,
  },
  actionButton: {
    marginHorizontal: 8,
  },
});

export default TaskDetailScreen;