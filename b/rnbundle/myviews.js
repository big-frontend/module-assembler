import { NativeModules, Button, requireNativeComponent } from "react-native";
import React from "react";
const MyImageView = requireNativeComponent("MyImageView");
class MyCustomView extends React.Component {
  constructor(props) {
    super(props);
    this._onChange = this._onChange.bind(this);
  }
  _onChange(event) {
    if (!this.props.onChangeMessage) {
      return;
    }
    this.props.onChangeMessage(event.nativeEvent.message);
  }
  render() {
    return <MyImageView {...this.props} onChange={this._onChange} />;
  }
}
module.exports = MyCustomView;
