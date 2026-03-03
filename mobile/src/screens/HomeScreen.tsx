import React from 'react';
import { View, StyleSheet, ScrollView, RefreshControl, TouchableOpacity } from 'react-native';
import { Text, Card, Button, Searchbar, IconButton } from 'react-native-paper';
import LazyImage from '../components/common/LazyImage';
import { useNavigation } from '@react-navigation/native';
import { StackNavigationProp } from '@react-navigation/stack';
import { RootStackParamList } from '../navigation/AppNavigator';
import { COLORS, WORKFLOW_CATEGORIES } from '../utils/constants';

type HomeScreenNavigationProp = StackNavigationProp<RootStackParamList, 'Main'>;

type ViewMode = 'grid' | 'list';

const HomeScreen = () => {
  const navigation = useNavigation<HomeScreenNavigationProp>();
  const [searchQuery, setSearchQuery] = React.useState('');
  const [selectedCategory, setSelectedCategory] = React.useState('all');
  const [refreshing, setRefreshing] = React.useState(false);
  const [viewMode, setViewMode] = React.useState<ViewMode>('grid');

  const onRefresh = React.useCallback(() => {
    setRefreshing(true);
    // 模拟网络请求
    setTimeout(() => {
      setRefreshing(false);
    }, 1000);
  }, []);

  // 模拟工作流数据
  const workflows = [
    {
      id: 'wf_001',
      name: 'AI视频人物换脸',
      description: '基于AI技术将视频中的人物面部替换为目标人物',
      category: 'hot',
      coverUrl: 'https://picsum.photos/200/300',
      basePoints: 1000,
    },
    {
      id: 'wf_002',
      name: '自媒体爆款标题生成',
      description: '一键生成吸睛的自媒体标题，提升点击率',
      category: 'self_media',
      coverUrl: 'https://picsum.photos/200/300?random=1',
      basePoints: 500,
    },
    {
      id: 'wf_003',
      name: '企业宣传片制作',
      description: '专业的商业宣传视频制作工具',
      category: 'business',
      coverUrl: 'https://picsum.photos/200/300?random=2',
      basePoints: 2000,
    },
    {
      id: 'wf_004',
      name: '小说推文视频生成',
      description: '将小说片段转换为吸引人的推文视频',
      category: 'novel',
      coverUrl: 'https://picsum.photos/200/300?random=3',
      basePoints: 800,
    },
    {
      id: 'wf_005',
      name: '动漫风格转换',
      description: '将真人照片转换为动漫风格',
      category: 'anime',
      coverUrl: 'https://picsum.photos/200/300?random=4',
      basePoints: 600,
    },
    {
      id: 'wf_006',
      name: '科幻场景生成',
      description: '创造未来感十足的科幻场景',
      category: 'scifi',
      coverUrl: 'https://picsum.photos/200/300?random=5',
      basePoints: 1200,
    },
    {
      id: 'wf_007',
      name: 'AI写真修图',
      description: '专业级人像写真修图工具',
      category: 'portrait',
      coverUrl: 'https://picsum.photos/200/300?random=6',
      basePoints: 700,
    },
    {
      id: 'wf_008',
      name: '萌宠视频剪辑',
      description: '快速制作可爱的宠物视频',
      category: 'pet',
      coverUrl: 'https://picsum.photos/200/300?random=7',
      basePoints: 500,
    },
  ];

  const filteredWorkflows = workflows.filter(workflow => {
    if (selectedCategory !== 'all' && workflow.category !== selectedCategory) {
      return false;
    }
    if (searchQuery && !workflow.name.includes(searchQuery)) {
      return false;
    }
    return true;
  });

  return (
    <View style={styles.container}>
      <View style={styles.tabContainer}>
        <ScrollView 
          horizontal 
          showsHorizontalScrollIndicator={false}
          contentContainerStyle={styles.tabContent}
        >
        <TouchableOpacity 
          style={[styles.tabItem, selectedCategory === 'all' && styles.tabItemActive]}
          onPress={() => setSelectedCategory('all')}
        >
          <Text style={[styles.tabText, selectedCategory === 'all' && styles.tabTextActive]}>
            全部
          </Text>
        </TouchableOpacity>
        {WORKFLOW_CATEGORIES.map(category => (
          <TouchableOpacity 
            key={category.id}
            style={[styles.tabItem, selectedCategory === category.id && styles.tabItemActive]}
            onPress={() => setSelectedCategory(category.id)}
          >
            <Text style={[styles.tabText, selectedCategory === category.id && styles.tabTextActive]}>
              {category.name}
            </Text>
          </TouchableOpacity>
        ))}
        </ScrollView>
      </View>

      <ScrollView
        style={styles.scrollView}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
        }
      >
        {viewMode === 'grid' ? (
          <View style={styles.workflowGrid}>
            {filteredWorkflows.map((workflow) => (
              <Card key={workflow.id} style={styles.workflowCard} onPress={() => navigation.navigate('WorkflowDetail', { workflowId: workflow.id })}>
                <View style={styles.cardCover}>
                  <LazyImage source={{ uri: workflow.coverUrl }} style={styles.coverImage} />
                </View>
                <Card.Content>
                  <Text style={styles.workflowName}>{workflow.name}</Text>
                  <Text style={styles.workflowDescription} numberOfLines={2}>
                    {workflow.description}
                  </Text>
                  <Text style={styles.pointsText}>消耗: {workflow.basePoints} 资源点</Text>
                </Card.Content>
                <Card.Actions>
                  <Button mode="contained" style={styles.useButton}>
                    立即使用
                  </Button>
                </Card.Actions>
              </Card>
            ))}
          </View>
        ) : (
          <View style={styles.workflowList}>
            {filteredWorkflows.map((workflow) => (
              <Card key={workflow.id} style={styles.workflowListCard} onPress={() => navigation.navigate('WorkflowDetail', { workflowId: workflow.id })}>
                <Card.Content style={styles.listCardContent}>
                  <LazyImage source={{ uri: workflow.coverUrl }} style={styles.listCoverImage} />
                  <View style={styles.listCardInfo}>
                    <Text style={styles.workflowName}>{workflow.name}</Text>
                    <Text style={styles.workflowDescription} numberOfLines={2}>
                      {workflow.description}
                    </Text>
                    <Text style={styles.pointsText}>消耗: {workflow.basePoints} 资源点</Text>
                  </View>
                </Card.Content>
                <Card.Actions>
                  <Button mode="contained" style={styles.useButton}>
                    立即使用
                  </Button>
                </Card.Actions>
              </Card>
            ))}
          </View>
        )}
      </ScrollView>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: COLORS.background,
  },
  tabContainer: {
    height: 36,
    paddingTop: 4,
    marginBottom: 8,
  },
  tabContent: {
    paddingHorizontal: 16,
    alignItems: 'center',
    paddingBottom: 4,
  },
  tabItem: {
    paddingHorizontal: 14,
    paddingVertical: 4,
    marginRight: 6,
    borderRadius: 14,
    backgroundColor: COLORS.surface,
    height: 28,
    justifyContent: 'center',
  },
  tabItemActive: {
    backgroundColor: COLORS.primary,
  },
  tabText: {
    fontSize: 13,
    color: COLORS.textSecondary,
  },
  tabTextActive: {
    color: COLORS.text,
    fontWeight: '600',
  },
  scrollView: {
    flex: 1,
  },
  workflowGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    paddingHorizontal: 12,
    paddingBottom: 20,
  },
  workflowCard: {
    width: '48%',
    margin: '1%',
    backgroundColor: COLORS.surface,
    elevation: 3,
    borderRadius: 12,
    overflow: 'hidden',
  },
  cardCover: {
    width: '100%',
    height: 160,
  },
  coverImage: {
    height: 160,
    borderTopLeftRadius: 12,
    borderTopRightRadius: 12,
  },
  workflowName: {
    fontSize: 16,
    fontWeight: 'bold',
    color: COLORS.text,
    marginBottom: 6,
  },
  workflowDescription: {
    fontSize: 13,
    color: COLORS.textSecondary,
    marginBottom: 10,
    lineHeight: 18,
  },
  pointsText: {
    fontSize: 12,
    color: COLORS.primary,
    marginBottom: 12,
    fontWeight: '500',
  },
  useButton: {
    flex: 1,
    borderRadius: 8,
    paddingVertical: 6,
  },
  workflowList: {
    paddingHorizontal: 16,
    paddingBottom: 20,
  },
  workflowListCard: {
    marginBottom: 16,
    backgroundColor: COLORS.surface,
    elevation: 3,
    borderRadius: 12,
  },
  listCardContent: {
    flexDirection: 'row',
    paddingVertical: 8,
  },
  listCoverImage: {
    width: 120,
    height: 120,
    borderRadius: 8,
    marginRight: 16,
  },
  listCardInfo: {
    flex: 1,
    justifyContent: 'center',
  },
});

export default HomeScreen;