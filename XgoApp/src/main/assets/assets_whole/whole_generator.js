'use strict';

//运动
Blockly.JavaScript['move_direction_speed'] = function(block) {
  var direction = block.getFieldValue('direction');
  var speed = block.getFieldValue('speed');
  var code = 'move.move_direction_speed('+direction+','+speed+'\);\n';
  return code;
};
Blockly.JavaScript['move_direction_speed_time'] = function(block) {
  var direction = block.getFieldValue('direction');
  var speed = block.getFieldValue('speed');
  var time = block.getFieldValue('time');
  var code = 'move.move_direction_speed_time('+direction+','+speed+','+time+'\);\n';
  return code;
};
Blockly.JavaScript['move_zero_speed'] = function(block) {
  var speed = block.getFieldValue('speed');
  var code = 'move.move_zero_speed('+speed+'\);\n';
  return code;
};
Blockly.JavaScript['move_zero_speed_time'] = function(block) {
  var speed = block.getFieldValue('speed');
  var time = block.getFieldValue('time');
  var code = 'move.move_zero_speed_time('+speed+','+time+'\);\n';
  return code;
};
Blockly.JavaScript['move_stop'] = function(block) {

  var code = 'move.move_stop('+'\);\n';
  return code;
};
Blockly.JavaScript['shake_direction_speed'] = function(block) {
  var direction = block.getFieldValue('direction');
  var speed = block.getFieldValue('speed');
  var code = 'move.shake_direction_speed('+direction+','+speed+'\);\n';
  return code;
};
Blockly.JavaScript['shake_direction_speed_time'] = function(block) {
  var direction = block.getFieldValue('direction');
  var speed = block.getFieldValue('speed');
  var time = block.getFieldValue('time');
  var code = 'move.shake_direction_speed_time('+direction+','+speed+','+time+'\);\n';
  return code;
};
Blockly.JavaScript['shake_stop'] = function(block) {
  var code = 'move.shake_stop('+'\);\n';
  return code;
};
Blockly.JavaScript['all_stop'] = function(block) {
  var code = 'move.all_stop('+'\);\n';
  return code;
};
Blockly.JavaScript['open_imu'] = function(block) {
 var imu = block.getFieldValue('imu');
  var code = 'move.open_imu('+imu+'\);\n';
  return code;
};
Blockly.JavaScript['state_imu'] = function(block)
{
  var code = '{move.state_imu('+'\);}';
  return code;
};

//传感器
Blockly.JavaScript['sensor_avoid'] = function(block) {
var which = block.getFieldValue('which');
  var code = 'move.sensor_avoid('+which+'\);';
  return [code];
};
//声光
Blockly.JavaScript['open_led'] = function(block) {
var state = block.getFieldValue('state');
var which = block.getFieldValue('which');
  var code = 'move.open_led('+state+','+which+'\);\n';
  return code;
};
Blockly.JavaScript['state_led'] = function(block) {
var which = block.getFieldValue('which');
  var code = 'move.state_led('+which+'\);';
  return [code];
};
//逻辑
Blockly.JavaScript['control_wait'] = function(block) {
var time = block.getFieldValue('time');
  var code = 'move.control_wait('+time+'\);\n';
  return code;
};

//自定义