import React, { useState, useEffect } from 'react';
import { View, StyleSheet, ScrollView, RefreshControl, TouchableOpacity } from 'react-native';
import { Text, Card, Button, Searchbar, IconButton } from 'react-native-paper';
import { SafeAreaView } from 'react-native-safe-area-context';
import LazyImage from '../components/common/LazyImage';
import MasonryList from '../components/common/MasonryList';
import { useNavigation } from '@react-navigation/native';
import { StackNavigationProp } from '@react-navigation/stack';
import { RootStackParamList } from '../navigation/AppNavigator';
import { COLORS, WORKFLOW_CATEGORIES } from '../utils/constants';
import { workflowApi } from '../services/api';

type HomeScreenNavigationProp = StackNavigationProp<RootStackParamList, 'Main'>;

type ViewMode = 'grid' | 'list';

const HomeScreen = () => {
  const navigation = useNavigation<HomeScreenNavigationProp>();
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [refreshing, setRefreshing] = useState(false);
  const [viewMode, setViewMode] = useState<ViewMode>('grid');
  const [workflows, setWorkflows] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const formatNumber = (num: number): string => {
    if (num >= 10000) {
      return (num / 10000).toFixed(1) + '万';
    }
    if (num >= 1000) {
      return (num / 1000).toFixed(1) + 'k';
    }
    return num.toString();
  };

  const loadWorkflows = async (refresh: boolean = false) => {
    if (loading) return;

    try {
      setLoading(true);
      setError(null);
      const currentPage = refresh ? 0 : page;

      const response = await workflowApi.getWorkflows({
        page: currentPage,
        size: 20,
        category: selectedCategory === 'all' ? undefined : selectedCategory,
        keyword: searchQuery || undefined
      });

      if (refresh) {
        setWorkflows(response.data);
        setPage(1);
      } else {
        setWorkflows(prev => [...prev, ...response.data]);
        setPage(currentPage + 1);
      }

      setHasMore(response.data.length === 20);
    } catch (err: any) {
      setError(err.message || '加载失败');
      console.error('加载工作流失败:', err);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  useEffect(() => {
    loadWorkflows(true);
  }, [selectedCategory, searchQuery]);

  const onRefresh = () => {
    setRefreshing(true);
    loadWorkflows(true);
  };

  const filteredWorkflows = workflows; // 已经在 API 层过滤

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      <View style={styles.header}>
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
    paddingBottom: 12,
  },
  tabContainer: {
    paddingTop: 8,
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
  scrollView: {
    flex: 1,
    backgroundColor: '#0F0F10',
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