import React from 'react';
import { View, StyleSheet, ScrollView, RefreshControl, TouchableOpacity } from 'react-native';
import { Text, Card, Button, Searchbar, IconButton } from 'react-native-paper';
import LazyImage from '../components/common/LazyImage';
import MasonryList from '../components/common/MasonryList';
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

  const formatNumber = (num: number): string => {
    if (num >= 10000) {
      return (num / 10000).toFixed(1) + '万';
    }
    if (num >= 1000) {
      return (num / 1000).toFixed(1) + 'k';
    }
    return num.toString();
  };

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
      usageCount: 12580,
      favoriteCount: 3421,
    },
    {
      id: 'wf_002',
      name: '自媒体爆款标题生成',
      description: '一键生成吸睛的自媒体标题',
      category: 'self_media',
      coverUrl: 'https://picsum.photos/200/300?random=1',
      usageCount: 8960,
      favoriteCount: 2156,
    },
    {
      id: 'wf_003',
      name: '企业宣传片制作',
      description: '专业的商业宣传视频制作工具，支持多种模板和风格定制',
      category: 'business',
      coverUrl: 'https://picsum.photos/200/300?random=2',
      usageCount: 5432,
      favoriteCount: 1890,
    },
    {
      id: 'wf_004',
      name: '小说推文视频生成',
      description: '将小说片段转换为吸引人的推文视频',
      category: 'novel',
      coverUrl: 'https://picsum.photos/200/300?random=3',
      usageCount: 15670,
      favoriteCount: 4523,
    },
    {
      id: 'wf_005',
      name: '动漫风格转换',
      description: '真人照片秒变动漫风格，支持多种二次元画风',
      category: 'anime',
      coverUrl: 'https://picsum.photos/200/300?random=4',
      usageCount: 9845,
      favoriteCount: 2987,
    },
    {
      id: 'wf_006',
      name: '科幻场景生成',
      description: '创造未来感十足的科幻场景',
      category: 'scifi',
      coverUrl: 'https://picsum.photos/200/300?random=5',
      usageCount: 6234,
      favoriteCount: 1765,
    },
    {
      id: 'wf_007',
      name: 'AI写真修图',
      description: '专业级人像写真修图工具，一键美颜磨皮',
      category: 'portrait',
      coverUrl: 'https://picsum.photos/200/300?random=6',
      usageCount: 11230,
      favoriteCount: 3654,
    },
    {
      id: 'wf_008',
      name: '萌宠视频剪辑',
      description: '快速制作可爱的宠物视频',
      category: 'pet',
      coverUrl: 'https://picsum.photos/200/300?random=7',
      usageCount: 7890,
      favoriteCount: 2341,
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
          <MasonryList
            data={filteredWorkflows}
            numColumns={2}
            columnGap={8}
            renderItem={(workflow) => (
              <Card style={styles.workflowCard} onPress={() => navigation.navigate('WorkflowDetail', { workflowId: workflow.id })}>
                <View style={styles.cardCover}>
                  <LazyImage source={{ uri: workflow.coverUrl }} style={styles.coverImage} />
                </View>
                <Card.Content>
                  <Text style={styles.workflowName}>{workflow.name}</Text>
                  <Text style={styles.workflowDescription} numberOfLines={2}>
                    {workflow.description}
                  </Text>
                  <View style={styles.statsContainer}>
                    <View style={styles.statItem}>
                      <Text style={styles.statIcon}>🔥</Text>
                      <Text style={styles.statText}>{formatNumber(workflow.usageCount)}</Text>
                    </View>
                    <View style={styles.statItem}>
                      <Text style={styles.statIcon}>❤️</Text>
                      <Text style={styles.statText}>{formatNumber(workflow.favoriteCount)}</Text>
                    </View>
                  </View>
                </Card.Content>
              </Card>
            )}
          />
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
                    <View style={styles.statsContainer}>
                      <View style={styles.statItem}>
                        <Text style={styles.statIcon}>👁️</Text>
                        <Text style={styles.statText}>{formatNumber(workflow.usageCount)}</Text>
                      </View>
                      <View style={styles.statItem}>
                        <Text style={styles.statIcon}>❤️</Text>
                        <Text style={styles.statText}>{formatNumber(workflow.favoriteCount)}</Text>
                      </View>
                    </View>
                  </View>
                </Card.Content>
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
  workflowCard: {
    width: '100%',
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
  statsContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 16,
    marginTop: 4,
  },
  statItem: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4,
  },
  statIcon: {
    fontSize: 14,
  },
  statText: {
    fontSize: 12,
    color: COLORS.textSecondary,
    fontWeight: '500',
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