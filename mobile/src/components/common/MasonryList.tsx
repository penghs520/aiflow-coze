import React, { useState, useEffect } from 'react';
import { View, StyleSheet, ViewStyle } from 'react-native';

interface MasonryListProps {
  data: any[];
  numColumns?: number;
  renderItem: (item: any, index: number) => React.ReactElement;
  columnGap?: number;
  style?: ViewStyle;
}

const MasonryList: React.FC<MasonryListProps> = ({
  data,
  numColumns = 2,
  renderItem,
  columnGap = 8,
  style,
}) => {
  const [columns, setColumns] = useState<any[][]>([]);

  useEffect(() => {
    // 初始化列数组
    const cols: any[][] = Array.from({ length: numColumns }, () => []);

    // 将数据分配到各列
    data.forEach((item, index) => {
      const columnIndex = index % numColumns;
      cols[columnIndex].push({ item, index });
    });

    setColumns(cols);
  }, [data, numColumns]);

  return (
    <View style={[styles.container, style]}>
      {columns.map((column, columnIndex) => (
        <View
          key={columnIndex}
          style={[
            styles.column,
            {
              marginRight: columnIndex < numColumns - 1 ? columnGap : 0,
            },
          ]}
        >
          {column.map(({ item, index }) => (
            <View key={index} style={styles.item}>
              {renderItem(item, index)}
            </View>
          ))}
        </View>
      ))}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    paddingHorizontal: 12,
    paddingBottom: 20,
  },
  column: {
    flex: 1,
  },
  item: {
    marginBottom: 8,
  },
});

export default MasonryList;
