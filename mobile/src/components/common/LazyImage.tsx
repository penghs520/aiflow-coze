import React, { useState, useRef, useEffect } from 'react';
import { Image, View, StyleSheet, ActivityIndicator } from 'react-native';
import { COLORS } from '../../utils/constants';

interface LazyImageProps {
  source: { uri: string };
  style?: any;
  placeholderColor?: string;
  resizeMode?: 'cover' | 'contain' | 'stretch' | 'center';
}

const LazyImage: React.FC<LazyImageProps> = ({
  source,
  style,
  placeholderColor = COLORS.border,
  resizeMode = 'cover',
}) => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const imageRef = useRef<Image>(null);

  const handleLoad = () => {
    setLoading(false);
    setError(false);
  };

  const handleError = () => {
    setLoading(false);
    setError(true);
  };

  return (
    <View style={[styles.container, style]}>
      {loading && (
        <View style={[styles.placeholder, { backgroundColor: placeholderColor }]}>
          <ActivityIndicator size="small" color={COLORS.primary} />
        </View>
      )}
      {error && (
        <View style={[styles.placeholder, { backgroundColor: placeholderColor }]}>
          <Image
            source={{ uri: 'https://via.placeholder.com/100?text=Error' }}
            style={StyleSheet.absoluteFillObject}
            resizeMode={resizeMode}
          />
        </View>
      )}
      <Image
        ref={imageRef}
        source={source}
        style={[StyleSheet.absoluteFillObject, { opacity: loading ? 0 : 1 }]}
        resizeMode={resizeMode}
        onLoad={handleLoad}
        onError={handleError}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    position: 'relative',
    overflow: 'hidden',
  },
  placeholder: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
});

export default LazyImage;