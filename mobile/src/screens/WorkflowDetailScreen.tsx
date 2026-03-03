import React, { useEffect, useState } from 'react';
import { View, StyleSheet, ScrollView, Image, Platform } from 'react-native';
import { Text, Card, Button, Divider } from 'react-native-paper';
import { useRoute, useNavigation, RouteProp } from '@react-navigation/native';
import { StackNavigationProp } from '@react-navigation/stack';
import { RootStackParamList } from '../navigation/AppNavigator';
import { COLORS } from '../utils/constants';

type WorkflowDetailScreenRouteProp = RouteProp<RootStackParamList, 'WorkflowDetail'>;
type WorkflowDetailScreenNavigationProp = StackNavigationProp<RootStackParamList, 'WorkflowDetail'>;

const WorkflowDetailScreen = () => {
  const route = useRoute<WorkflowDetailScreenRouteProp>();
  const navigation = useNavigation<WorkflowDetailScreenNavigationProp>();
  const { workflowId } = route.params;
  const [workflow, setWorkflow] = useState<any>(null);

  useEffect(() => {
    // 模拟获取工作流详情
    setTimeout(() => {
      setWorkflow({
        id: workflowId,
        name: 'AI视频人物换脸',
        description: '基于AI技术将视频中的人物面部替换为目标人物，支持多种视频格式和分辨率。',
        category: 'video_creation',
        tags: ['换脸', '视频编辑', 'AI特效'],
        coverUrl: 'https://picsum.photos/400/200',
        previewVideoUrl: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4',
        basePoints: 1000,
        parameters: [
          {
            key: 'source_video',
            name: '源视频',
            type: 'video_file',
            required: true,
            constraints: {
              maxSize: '100MB',
              formats: ['mp4', 'mov', 'avi']
            }
          },
          {
            key: 'target_face_image',
            name: '目标人脸图片',
            type: 'image_file',
            required: true,
            constraints: {
              maxSize: '10MB',
              formats: ['jpg', 'png', 'jpeg']
            }
          },
          {
            key: 'output_resolution',
            name: '输出分辨率',
            type: 'select',
            required: false,
            default: '720p',
            options: ['480p', '720p', '1080p', '2K', '4K']
          }
        ],
        statistics: {
          usageCount: 12543,
          averageRating: 4.8,
          favoriteCount: 3421
        }
      });
    }, 500);
  }, [workflowId]);

  if (!workflow) {
    return (
      <View style={styles.loadingContainer}>
        <Text style={styles.loadingText}>加载中...</Text>
      </View>
    );
  }

  return (
    <ScrollView style={styles.container}>
      <Image source={{ uri: workflow.coverUrl }} style={styles.coverImage} />

      <Card style={styles.infoCard}>
        <Card.Content>
          <Text style={styles.workflowName}>{workflow.name}</Text>
          <View style={styles.tagsContainer}>
            {workflow.tags.map((tag: string, index: number) => (
              <View key={index} style={styles.tag}>
                <Text style={styles.tagText}>{tag}</Text>
              </View>
            ))}
          </View>
          <Text style={styles.description}>{workflow.description}</Text>
          <Divider style={styles.divider} />
          <View style={styles.statsContainer}>
            <View style={styles.statItem}>
              <Text style={styles.statValue}>{workflow.statistics.usageCount}</Text>
              <Text style={styles.statLabel}>使用次数</Text>
            </View>
            <View style={styles.statItem}>
              <Text style={styles.statValue}>{workflow.statistics.averageRating}</Text>
              <Text style={styles.statLabel}>平均评分</Text>
            </View>
            <View style={styles.statItem}>
              <Text style={styles.statValue}>{workflow.statistics.favoriteCount}</Text>
              <Text style={styles.statLabel}>收藏数</Text>
            </View>
          </View>
        </Card.Content>
      </Card>

      <Card style={styles.parametersCard}>
        <Card.Content>
          <Text style={styles.sectionTitle}>参数说明</Text>
          {workflow.parameters.map((param: any, index: number) => (
            <View key={index} style={styles.paramItem}>
              <Text style={styles.paramName}>
                {param.name} {param.required && <Text style={styles.requiredMark}>*</Text>}
              </Text>
              <Text style={styles.paramType}>{param.type}</Text>
              {param.constraints && (
                <Text style={styles.paramConstraints}>
                  限制: {param.constraints.maxSize}, 格式: {param.constraints.formats?.join(', ')}
                </Text>
              )}
            </View>
          ))}
        </Card.Content>
      </Card>

      <View style={styles.actionContainer}>
        <Text style={styles.pointsText}>消耗: {workflow.basePoints} 资源点</Text>
        <Button mode="contained" style={styles.useButton} onPress={() => navigation.navigate('ParameterInput', { workflowId: workflow.id })}>
          立即使用
        </Button>
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
  previewVideo: {
    width: '100%',
    height: 200,
  },
  coverImage: {
    width: '100%',
    height: 200,
  },
  infoCard: {
    margin: 16,
    backgroundColor: COLORS.surface,
    elevation: 2,
  },
  workflowName: {
    fontSize: 20,
    fontWeight: 'bold',
    color: COLORS.text,
    marginBottom: 12,
  },
  tagsContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginBottom: 12,
  },
  tag: {
    backgroundColor: COLORS.primary + '20',
    paddingHorizontal: 12,
    paddingVertical: 4,
    borderRadius: 16,
    marginRight: 8,
    marginBottom: 8,
  },
  tagText: {
    fontSize: 12,
    color: COLORS.primary,
  },
  description: {
    fontSize: 14,
    color: COLORS.text,
    lineHeight: 20,
  },
  divider: {
    marginVertical: 16,
  },
  statsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-around',
  },
  statItem: {
    alignItems: 'center',
  },
  statValue: {
    fontSize: 18,
    fontWeight: 'bold',
    color: COLORS.text,
  },
  statLabel: {
    fontSize: 12,
    color: COLORS.textSecondary,
    marginTop: 4,
  },
  parametersCard: {
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
  paramItem: {
    marginBottom: 12,
  },
  paramName: {
    fontSize: 14,
    fontWeight: '500',
    color: COLORS.text,
  },
  requiredMark: {
    color: COLORS.error,
  },
  paramType: {
    fontSize: 12,
    color: COLORS.textSecondary,
    marginTop: 2,
  },
  paramConstraints: {
    fontSize: 12,
    color: COLORS.textSecondary,
    marginTop: 4,
  },
  actionContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    padding: 16,
    marginBottom: 20,
  },
  pointsText: {
    fontSize: 16,
    color: COLORS.primary,
    fontWeight: '500',
  },
  useButton: {
    paddingHorizontal: 32,
  },
});

export default WorkflowDetailScreen;