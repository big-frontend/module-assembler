import { StatusBar } from 'expo-status-bar';
import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { NativeModules, Button, requireNativeComponent } from "react-native";
import MyCustomView from './myviews';


export default function App() {
  const { CalendarModule } = NativeModules;
  const a = CalendarModule.createCalendarEvent("testName", "testLocation");
  console.log("cjf We will invoke the native module here!  "+a);
  return (
    <View style={styles.container}>
      <Text>Open up App.js to start working on your app!</Text>
      <StatusBar style="auto" />
      <MyCustomView></MyCustomView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});

