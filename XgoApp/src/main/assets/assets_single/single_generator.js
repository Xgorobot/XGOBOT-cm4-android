'use strict';

//运动
Blockly.JavaScript['input_position'] = function(block) {
  var which = block.getFieldValue('which');
  var top = block.getFieldValue('top');
  var middle = block.getFieldValue('middle');
  var bottom = block.getFieldValue('bottom');
  return "0";
};
Blockly.JavaScript['input_reset'] = function(block) {
  return "0";
};
//逻辑
Blockly.JavaScript['control_wait'] = function(block) {
var time = block.getFieldValue('time');
  return "0";
};
//自定义