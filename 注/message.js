import store from '@/store/index';

const webSocketUrl = 'ws://' + location.host + '/socket'+'/websocket'

var ws = null
var timeOut=0
export async function sendSocket(text,chatHistoryId){
  init(chatHistoryId)
  await waitForWebSocketConnection()
  // console.log(JSON.stringify(text))
  ws.send(JSON.stringify(text))
}
export function stopSocket(){
  if(!ws)return
  store.commit('clearMessage')
  ws.close()
}
function init(chatHistoryId){
  ws = new WebSocket(webSocketUrl+'/'+store.state.STUDENTID+'/'+chatHistoryId,[store.state.TOKEN])
  //onopen事件监听
  ws.addEventListener('open', e => {
    timeOut=new Date().getTime()
    judgeTimeout()
    console.log('与服务端连接打开->', e)
  }, false)
  //onclose事件监听
  ws.addEventListener('close', e => {
    store.commit('clearMessage')
    console.log('与服务端连接关闭->', e)
  }, false)
  //onmessage事件监听
  ws.addEventListener('message', e => {
    console.log('来自服务端的消息->', e)
    timeOut=new Date().getTime()
    if(e.data!='DONE'){
      store.commit('updateMessage',e.data)
    }
    else{
      store.commit('clearMessage')
      ws.close()
    }
  },false)
  //onerror事件监听
  ws.addEventListener('error', e => {
    console.log('与服务端连接异常->', e)
    store.commit('clearMessage')
  }, false)
}
function waitForWebSocketConnection() {
  return new Promise((resolve, reject) => {
    ws.onopen = () => {
      console.log('WebSocket connection succeeded');
      resolve();
    };
    ws.onerror = (error) => {
      console.error('WebSocket connection error', error);
      reject(error);
    };
  });
}
function judgeTimeout(){
  return setTimeout(() => {
    
    let newTime = new Date().getTime()
    console.log('查询是否超时',newTime,timeOut)
    if (newTime - timeOut > 5000000) {
      console.log('大模型超时')
      ws.close()
    }
    else {
      return judgeTimeout()
    }
    return
  }, 1000);
  
}