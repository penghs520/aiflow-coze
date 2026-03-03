import React, { useEffect, useState } from 'react';
import { View, StyleSheet, ScrollView } from 'react-native';
import { Text, Card, Button, TextInput, SegmentedButtons, Divider } from 'react-native-paper';
import { useRoute, useNavigation, RouteProp } from '@react-navigation/native';
import { StackNavigationProp } from '@react-navigation/stack';
import { RootStackParamList } from '../navigation/AppNavigator';
import { COLORS } from '../utils/constants';

type ParameterInputScreenRouteProp = RouteProp<RootStackParamList, 'ParameterInput'>;
type ParameterInputScreenNavigationProp = StackNavigationProp<RootStackParamList, 'ParameterInput'>;

const ParameterInputScreen = () => {
  const route = useRoute<ParameterInputScreenRouteProp>();
  const navigation = useNavigation<ParameterInputScreenNavigationProp>();
  const { workflowId } = route.params;
  const [workflow, setWorkflow] = useState<any>(null);
  const [parameters, setParameters] = useState<Record<string, any>>({});
  const [estimatedPoints, setEstimatedPoints] = useState(0);

  useEffect(() => {
    // 模拟获取工作流详情
    setTimeout(() => {
      const workflowData = {
        id: workflowId,
        name: 'AI视频人物换脸',
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
        ]
      };
      setWorkflow(workflowData);
      
      // 初始化参数
      const initialParams: Record<string, any> = {};
      workflowData.parameters.forEach((param: any) => {
        initialParams[param.key] = param.default || '';
      });
      setParameters(initialParams);
      
      // 计算初始资源点
      calculatePoints(initialParams, workflowData.basePoints);
    }, 500);
  }, [workflowId]);

  const calculatePoints = (params: Record<string, any>, basePoints: number) => {
    // 简单的资源点计算逻辑
    let points = basePoints;
    if (params.output_resolution === '1080p') {
      points += 500;
    } else if (params.output_resolution === '2K') {
      points += 1000;
    } else if (params.output_resolution === '4K') {
      points += 2000;
    }
    setEstimatedPoints(points);
  };

  const handleParameterChange = (key: string, value: any) => {
    const newParams = { ...parameters, [key]: value };
    setParameters(newParams);
    if (workflow) {
      calculatePoints(newParams, workflow.basePoints);
    }
  };

  const handleSubmit = () => {
    // 模拟提交任务
    navigation.navigate('Tasks');
  };

  if (!workflow) {
    return (
      <View style={styles.loadingContainer}>
        <Text style={styles.loadingText}>加载中...</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <ScrollView style={styles.scrollView}>
        <Card style={styles.formCard}>
          <Card.Content>
            <Text style={styles.workflowName}>{workflow.name}</Text>
            <Divider style={styles.divider} />
            
            {workflow.parameters.map((param: any, index: number) => (
              <View key={index} style={styles.paramContainer}>
                <Text style={styles.paramName}>
                  {param.name} {param.required && <Text style={styles.requiredMark}>*</Text>}
                </Text>
                
                {param.type === 'select' && (
                  <SegmentedButtons
                    value={parameters[param.key]}
                    onValueChange={(value) => handleParameterChange(param.key, value)}
                    buttons={param.options.map((option: string) => ({ value: option, label: option }))}
                    style={styles.segmentedButtons}
                  />
                )}
                
                {param.type === 'text' && (
                  <TextInput
                    value={parameters[param.key]}
                    onChangeText={(text) => handleParameterChange(param.key, text)}
                    style={styles.textInput}
                    placeholder={`请输入${param.name}`}
                  />
                )}
                
                {param.type === 'image_file' && (
                  <Button mode="outlined" style={styles.fileButton}>
                    选择图片
                  </Button>
                )}
                
                {param.type === 'video_file' && (
                  <Button mode="outlined" style={styles.fileButton}>
                    选择视频
                  </Button>
                )}
                
                {param.constraints && (
                  <Text style={styles.constraintsText}>
                    限制: {param.constraints.maxSize}, 格式: {param.constraints.formats?.join(', ')}
                  </Text>
                )}
              </View>
            ))}
          </Card.Content>
        </Card>
      </ScrollView>
      
      <View style={styles.bottomContainer}>
        <View style={styles.pointsContainer}>
          <Text style={styles.pointsLabel}>预估消耗</Text>
          <Text style={styles.pointsValue}>{estimatedPoints} 资源点</Text>
        </View>
        <Button mode="contained" style={styles.submitButton} onPress={handleSubmit}>
          提交任务
        </Button>
      </View>
    </View>
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
  scrollView: {
    flex: 1,
  },
  formCard: {
    margin: 16,
    backgroundColor: COLORS.surface,
    elevation: 2,
  },
  workflowName: {
    fontSize: 18,
    fontWeight: 'bold',
    color: COLORS.text,
  },
  divider: {
    marginVertical: 16,
  },
  paramContainer: {
    marginBottom: 20,
  },
  paramName: {
    fontSize: 14,
    fontWeight: '500',
    color: COLORS.text,
    marginBottom: 8,
  },
  requiredMark: {
    color: COLORS.error,
  },
  segmentedButtons: {
    marginBottom: 8,
  },
  textInput: {
    backgroundColor: COLORS.background,
    marginBottom: 8,
  },
  fileButton: {
    marginBottom: 8,
  },
  constraintsText: {
    fontSize: 12,
    color: COLORS.textSecondary,
    marginTop: 4,
  },
  bottomContainer: {
    backgroundColor: COLORS.surface,
    padding: 16,
    borderTopWidth: 1,
    borderTopColor: COLORS.border,
  },
  pointsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 16,
  },
  pointsLabel: {
    fontSize: 14,
    color: COLORS.textSecondary,
  },
  pointsValue: {
    fontSize: 16,
    fontWeight: 'bold',
    color: COLORS.primary,
  },
  submitButton: {
    paddingVertical: 8,
  },
});

export default ParameterInputScreen;