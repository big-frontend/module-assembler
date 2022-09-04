import { StatusBar } from 'expo-status-bar';
import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
// import { NativeModules, Button } from "react-native";
// import MyCustomView from './myviews';

import { WebView } from "react-native-webview";
export default function App() {
  // const { CalendarModule } = NativeModules;
  // const a = CalendarModule.createCalendarEvent("testName", "testLocation");
  // console.log("cjf We will invoke the native module here!  "+a);
  return (
    <View style={styles.container}>
      <Text>Open up App.js to start working on your app!</Text>
      <StatusBar style="auto" />
      {/* <MyCustomView></MyCustomView> */}
    </View>
    // <WebView
    //   source={{
    //     uri: "https://www.baidu.com",
    //   }}
    //   style={styles.container}
    // />
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

