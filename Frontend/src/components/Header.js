import tailwind from 'tailwind-rn';
import {View, Text} from 'react-native';
import React from 'react';

const Header = () => {
  return (
    <View
      accessibilityRole="header"
      style={tailwind('text-center bg-blue-800 text-white p-4 mb-10')}>
      <View style={tailwind('text-3xl font-bold mb-3')}>
        <Text>Covid-19 Tracker</Text>
      </View>
    </View>
  );
};

export default Header;
