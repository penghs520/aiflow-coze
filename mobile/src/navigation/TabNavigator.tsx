import React from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { Ionicons } from '@expo/vector-icons';
import { useNavigation, NavigationProp } from '@react-navigation/native';
import HomeScreen from '../screens/HomeScreen';
import TaskCenterScreen from '../screens/TaskCenterScreen';
import ProfileScreen from '../screens/ProfileScreen';
import { COLORS, STORAGE_KEYS } from '../utils/constants';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { RootStackParamList } from './AppNavigator';

const Tab = createBottomTabNavigator();

const TabNavigator = () => {
  const navigation = useNavigation<NavigationProp<RootStackParamList>>();

  const checkLoginAndNavigate = async (): Promise<boolean> => {
    const token = await AsyncStorage.getItem(STORAGE_KEYS.USER_TOKEN);
    if (!token) {
      navigation.navigate('Login');
      return false;
    }
    return true;
  };

  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        tabBarIcon: ({ focused, color, size }) => {
          let iconName: keyof typeof Ionicons.glyphMap = 'home';

          if (route.name === 'Home') {
            iconName = focused ? 'home' : 'home-outline';
          } else if (route.name === 'Tasks') {
            iconName = focused ? 'list' : 'list-outline';
          } else if (route.name === 'Profile') {
            iconName = focused ? 'person' : 'person-outline';
          }

          return <Ionicons name={iconName} size={size} color={color} />;
        },
        tabBarActiveTintColor: COLORS.primary,
        tabBarInactiveTintColor: COLORS.textSecondary,
        tabBarStyle: {
          backgroundColor: '#0F0F10',
          borderTopColor: '#0F0F10',
          paddingBottom: 5,
          paddingTop: 5,
          height: 60,
        },
        headerStyle: {
          backgroundColor: COLORS.surface,
          elevation: 0,
          shadowOpacity: 0,
          borderBottomWidth: 1,
          borderBottomColor: COLORS.border,
        },
        headerTintColor: COLORS.text,
        headerTitleStyle: {
          fontWeight: 'bold',
        },
      })}
    >
      <Tab.Screen name="Home" component={HomeScreen} options={{ headerShown: false }} />
      <Tab.Screen
        name="Tasks"
        component={TaskCenterScreen}
        options={{ title: '任务中心' }}
        listeners={{
          tabPress: async (e) => {
            e.preventDefault();
            const isLoggedIn = await checkLoginAndNavigate();
            if (isLoggedIn) {
              navigation.navigate('Main', { screen: 'Tasks' });
            }
          },
        }}
      />
      <Tab.Screen
        name="Profile"
        component={ProfileScreen}
        options={{ title: '我的', headerShown: false }}
        listeners={{
          tabPress: async (e) => {
            e.preventDefault();
            const isLoggedIn = await checkLoginAndNavigate();
            if (isLoggedIn) {
              navigation.navigate('Main', { screen: 'Profile' });
            }
          },
        }}
      />
    </Tab.Navigator>
  );
};

export default TabNavigator;