<template>
  <div style="height: 100%;">

    <el-container style="height: 100%;">
      <!-- 左侧导航栏 -->
      <el-main v-show="!IsLeftCollapsed"
        style="background-color: #161616;flex: 0 1 auto;margin-left: -20px;width: 300px;position: relative;">
        <div style="color: white;font-size: 20px;font-weight: bold;margin: 5px;">AURA-CAMPUS</div>
        <el-button type="info" @click="addChat()" class="addChat"
          style="margin:10px 5px 0px 10px;color: white;border-radius: 18px; font-size: 14px;padding: 12px;"
          icon="el-icon-circle-plus-outline">新建聊天
        </el-button>

        <div>
          <el-input v-model="historySearchInput" placeholder="Search" :clearable="true" prefix-icon="el-icon-search"
            style="margin: 10px 10px 0 10px;width: 240px;"></el-input>
        </div>
        <hr style="margin: 10px auto;width: 90%;" color="#424242">

        <div style="color: white;font-size: 18px;font-weight: bold;margin: 15px;">Chat History</div>

        <el-scrollbar style="height: 31%;width: 100%;overflow: hidden;">
          <div v-for="(item,index) in chatHistory" :key="item.id"
            @click="messages.length > 0 && messages[messages.length - 1].loading === true ? selectIndex =selectIndex:selectIndex=index"
            style="margin-left: 15px;font-size: 13px;width: 60%;display: flex;"
            :class="{ 'selected-chat': selectIndex === index, 'unselected-chat': selectIndex != index }">
            <div class="text-hidden" style="width: 200px;">{{ item.name }}</div>
            <div v-if="selectIndex === index" style="margin-left: auto;margin-right: 10px;"><i
                @click="modifyChat(item.id,item.name)" class="el-icon-edit"></i>
            </div>
            <div v-if="selectIndex === index"><i @click="deleteChat(item.id)" class="el-icon-delete"></i></div>
          </div>
        </el-scrollbar>
        <div class="bottom-container">
          <hr style="margin: 10px auto;margin-top: auto;width: 90%;" color="#424242">
          <div style="color: white;font-size: 18px;font-weight: bold;margin: 10px;">Other</div>
          <div class="s-box" style="font-size: 13px;" @click="toStudyPath();"> <i class="el-icon-document"
              style="margin-right: 10px;"></i>学习路径表</div>
          <div class="s-box" style="font-size: 13px;"><i class="el-icon-share" style="margin-right: 10px;"></i>学习论坛
          </div>

          <hr style="margin: 10px auto;width: 90%;" color="#424242">
          <div style="color: white;font-size: 18px;font-weight: bold;margin: 10px;">Settings</div>
          <div class="s-box" style="font-size: 13px;" @click="clearChat">
            <i class="el-icon-delete" style="margin-right: 10px;"></i> 清空对话
          </div>
          <div class="s-box" style="font-size: 13px;" @click="settingVisible=true">
            <i class="el-icon-setting" style="margin-right: 10px;"></i> 设置
          </div>
          <el-dropdown style="padding-top: 15px;">
            <div style="display: flex;align-items: center; ">
              <div class="avatar">
                <i class="el-icon-user-solid"></i>
              </div>
              <div style="color: #C1C1C1;">{{ studentId }}</div>
            </div>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item icon="el-icon-error" @click.native="editPasswordVisible=true">修改密码</el-dropdown-item>
              <el-dropdown-item icon="el-icon-error" @click.native="exitLogin">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>

      </el-main>
      <!-- 中间 -->
      <el-main style="background-color: #282828;height: 100%;flex: 2 1 auto;position: relative;z-index: 1">
        <div style="display: flex;align-items: center;width: 100%;justify-content: center">
          <!-- 左侧折叠 -->
          <div style="margin-right: auto;" class="collapse" @click="leftCollapse()">
            <i class="el-icon-s-fold" v-if="!IsLeftCollapsed"></i>
            <i class="el-icon-s-unfold" v-else></i>
          </div>
          <div v-if="chatHistory[selectIndex]"
            style="padding: 12px 15px; background-color: #161616;border-radius: 24px;width: 60%;color: white;text-align: center;font-weight: bold;"
            class="text-hidden">{{ chatHistory[selectIndex].name}}
          </div>
          <!-- 右侧折叠 -->
          <div style="margin-left: auto;" class="collapse" @click="rightCollapse()">
            <i class="el-icon-s-unfold" v-if="!IsRightCollapsed"></i>
            <i class="el-icon-s-fold" v-else></i>
          </div>

        </div>
        <!-- 输入框 -->
        <div
          style="display: flex;position: absolute;bottom: 5%;width: 100%;justify-content: center;align-items: center;z-index: 10;">
          <el-input style="width: 60%;" v-model="inputMessage" type="textarea" resize="none"
            :autosize="{ minRows: 1, maxRows: 6 }" placeholder="请输入一条消息"
            @keydown.enter.native="enterEvent($event)"></el-input>
          <el-button @click="sendMessage()" icon="el-icon-s-promotion"
            style="border-radius: 50%;margin-left: 20px;padding: 10px;"></el-button>
        </div>
        <el-scrollbar style="overflow: hidden;height: 85%;" ref="mainScroll">
          <div v-for="(item) in messages" :key="item.id" style="margin: 5px;">
            <!-- 用户说的话 -->
            <div style="display: flex;justify-content: flex-end;align-items:flex-start;">
              <div class="message" style="padding: 10px 10px;margin-top: 15px;">{{ item.userChat }}</div>
              <div
                style="background-color: #424242;color: white;border-radius: 50%;text-align: center;margin:10px;font-size: 31px;">
                <i class="el-icon-user-solid" style="padding: 7px;"></i>
              </div>
            </div>
            <!-- 机器人说的话 -->
            <div style="display: flex;align-items: flex-start;margin-top: 15px;">
              <div style="background-color: #424242;color: white;border-radius: 50%;margin:10px;width: 45px;height: 45px;aspect-ratio: 1/1;display: flex;
                justify-content: center;align-items: center;">
                <!-- <svg t="1711013466654" class="icon" viewBox="0 0 1024 1024" version="1.1"
                  xmlns="http://www.w3.org/2000/svg" p-id="4937" width="33" height="33">
                  <path
                    d="M960.55 538.483h-64.3v-270h-323.1v-55.8h163.6v-100h-449v100h165.4v55.8h-325v270h-64.7v100h64.7v269.9h768.1v-269.9h64.3v-100zM357.854 669.387c-44.649 0-80.904-36.255-80.904-80.904s36.255-80.904 80.904-80.904 80.904 36.255 80.904 80.904-36.256 80.904-80.904 80.904z m309.392 0c-44.649 0-80.904-36.255-80.904-80.904s36.255-80.904 80.904-80.904c44.649 0 80.904 36.255 80.904 80.904s-36.255 80.904-80.904 80.904z"
                    p-id="4938" fill="#FFFFFF" data-spm-anchor-id="a313x.search_index.0.i2.40213a81U4bapV"
                    class="selected"></path>
                </svg> -->
                <img src="../assets/roboticon.png" class="roboticon" alt="logo">
              </div>
              <!-- 测试 -->
              <!-- <div><el-input type="textarea" v-model="item.aiChat"></el-input> </div> -->
              <div class="message" style="margin-top: 10px;">
                <v-md-preview v-katex :text="item.aiChat"></v-md-preview>
                <div v-show="item.loading" style="float: right;">
                  <i class="el-icon-loading"></i>
                </div>
              </div>
            </div>

          </div>
        </el-scrollbar>
      </el-main>
      <!-- 右侧导航栏 -->
      <el-main v-show="!IsRightCollapsed" style="height: 100%;flex: 0 1 auto;width: 250px;min-width: 250px;">
        <div style="height: 40%;overflow: hidden;">
          <div style="margin-top: 20px;color: white;font-size: 20px;font-weight: bold;">学校题库题目</div>
          <el-input type="info" @keydown.enter.native="searchProblem" prefix-icon="el-icon-search"
            placeholder="请搜索id或题目" v-model="problemSearchInput" style="height:30px;margin: 15px 0;"></el-input>

          <div v-if="problems.length===0" style="color: #424242;font-size: 12px;margin:10px 0 0 10px">列表为空，请搜索id或题目
          </div>
          <div v-for="(item,index) in problems" :key="item.id" style="display: flex;width: 90%;align-items: center;">
            <div style="color: white;margin-top: 10px;font-size: 13px;width: 90%;cursor: pointer;"
              class="text-hidden s-box" @click="showProblemQuestion(item.id)">
              {{ index+1 }}.{{item.name }}</div>
            <el-button icon="el-icon-zoom-in" @click="showProblemQuestion(item.id)"
              style="border-radius: 50%;margin-left: 10px;padding: 5px;margin-top: 8px;"></el-button>
          </div>
        </div>

        <hr style="margin: 10px auto;width: 96%;" color="#424242">
        <div style="height: 25%;overflow: hidden;">
          <div style="margin: 5px;color: #C1C1C1;font-size: 18px;">
            <span style="vertical-align: middle;">我的收藏夹</span>
            <i class="el-icon-star-on" style="color:#ffcc00; font-size: 22px;vertical-align: middle; "></i>
          </div>
          <div v-for="(item,index) in collections" :key="item.id"
            style="margin: 5px;color: #C1C1C1;font-size: 12px;display: flex;align-items: center;">
            <div class="text-hidden" style="margin: 5px;">{{ index+1 }}.{{ item.name }}</div>
            <i @click="showProblemQuestion(item.id)" class="el-icon-upload2"></i>
          </div>
        </div>

        <hr style="margin: 10px auto;width: 96%;" color="#424242">
        <div style="height: 25%;overflow: hidden;">
          <div style="margin: 5px;color: #C1C1C1;font-size: 18px;margin-bottom: 10px">学生常问问题</div>

          <div v-for="(item,index) in studentProblems" :key="item.id"
            style="margin: 5px;color: #C1C1C1;font-size: 12px;display: flex;align-items: center;">
            <div class="text-hidden" style="margin: 5px;">{{ index+1 }}.{{ item.description }}</div>
            <i @click="handleProblemQuestion(item.description)" class="el-icon-upload2"></i>
          </div>
        </div>
      </el-main>
      <!-- 设置 -->
      <ChatSetting v-model="settingVisible"></ChatSetting>
      <!-- 修改名称 -->
      <ModifyChatName v-model="modifyChatVisible" ref="modifyChat" :fatherMethod="handleChatNameModify">
      </ModifyChatName>
      <!-- 弹窗 -->
      <PopUp :title="popUpTitle" :fatherMethod="handlePopUp" v-model="popUpVisible"></PopUp>
      <!-- 问题弹窗 -->
      <ProblemQuestion v-model="problemQuestionVisible" ref="problemQuestion" :fatherMethod="handleProblemQuestion">
      </ProblemQuestion>
      <!-- 本题常见问题 -->

      <PopUp title="修改密码" :fatherMethod="handleEditPassword" v-model="editPasswordVisible">
        <div style="display: flex;flex-direction: column;gap: 20px;">
          <div style="width: 90%;">
            <div style="color: white;margin-bottom: 10px;">旧密码</div>
            <el-input placeholder="请输入旧密码" v-model="editPasswordForm.oldPassword" show-password></el-input>
          </div>
          <div style="width: 90%;">
            <div style="color: white;margin-bottom: 10px;">新密码</div>
            <el-input placeholder="请输入新密码" v-model="editPasswordForm.newPassword" show-password></el-input>
          </div>
        </div>
      </PopUp>
    </el-container>
  </div>
</template>

<script>
import { addChatToServe, clearChatToServe, deleteChatToServe, getChatDetailsFromServe, modifyChatNameToServe, queryChatFromServe, searchChat } from '@/api/home.js';
import { editPasswordToServer } from '@/api/login';
import { sendSocket, stopSocket } from '@/api/message.js';
import { getCommonFromServe, searchProblemFromServe,getCollectionFromServe } from '@/api/problem.js'
import ChatSetting from '@/components/ChatSetting.vue';
import ModifyChatName from '@/components/ModifyChatName.vue';
import PopUp from '@/components/PopUp.vue';
import ProblemQuestion from '@/components/ProblemQuestion.vue';

export default {

  data(){
    return{
      studentId:'',
      selectIndex:0,
      IsLeftCollapsed: false,
      IsRightCollapsed: false,
      chatHistory:[

      ],
      messages:[
        
      ],
      historySearchInput:'',
      problemSearchInput:'',
      inputMessage:'',
      problems: [],
      studentProblems: [],
      contextBackground:'你现在是一个C++老师，你要回答学生问的关于编程方面的问题，如果不是编程有关的问题请拒绝回答，而且对于每个问题，不能直接给代码，而要给出思路',
      settingVisible:false,
      modifyChatVisible:false,
      handlePopUp:()=>{},
      popUpTitle:'',
      popUpVisible:false,
      problemQuestionVisible:false,
      collections:[],
      direction: 'rtl',
      editPasswordVisible:false,
      editPasswordForm:{
        oldPassword:'',
        newPassword:'',
        studentId:''
      }
    }
  },
  methods:{
    
    toStudyPath(){
      this.$router.push('/study')
    },
    leftCollapse(){
      this.IsLeftCollapsed=!this.IsLeftCollapsed
    },
    rightCollapse(){
      this.IsRightCollapsed=!this.IsRightCollapsed
    },
    async addChat(){
      await addChatToServe()
      await this.queryChat()
      this.selectIndex=0
    },
    async queryChat(){
      let res=await queryChatFromServe()
      this.chatHistory=res.data.data
    },
    sendMessage() {
      
      if (this.inputMessage.trim().length === 0) {
        this.$message('请输入一条消息')
        return
      }
      else if(this.chatHistory.length===0){
        this.$message('请新建聊天')
        return
      }
      if (this.messages.length>0&&this.messages[this.messages.length - 1].loading === true) return

      if(this.messages.length===0){
        modifyChatNameToServe(this.chatHistory[this.selectIndex].id, this.inputMessage.slice(0,20))
        this.chatHistory[this.selectIndex].name = this.inputMessage.slice(0, 20)
      }


      this.messages.push({id:this.messages.length+1,userChat:this.inputMessage,aiChat:'',loading:true})
      sendSocket(this.messageContext,this.chatHistory[this.selectIndex].id)
      this.inputMessage=''
    },
    bottomScroll() {
      console.log(this.$refs.mainScroll)
      this.$nextTick(() => {
        this.$refs['mainScroll'].wrap.scrollTop = this.$refs['mainScroll'].wrap.scrollHeight
      });
    },
    async getChatDetail(){
      // console.log(this.chatHistory)
      let res=await getChatDetailsFromServe(this.chatHistory[this.selectIndex].id)
      console.log(res)
      this.messages=res.data.data
    },
    async clearChat(){
      this.popUpTitle='是否清空所有对话'
      this.popUpVisible=true;
      this.handlePopUp=async ()=>{
        let ids=this.chatHistory.map((e)=>{
          return e.id
        })
        await clearChatToServe(ids)
        this.messages=[]
        await this.queryChat()
      }


    },
    async deleteChat(chatHistoryId) {
      console.log('deleteChat')
      this.messages=[]
      await deleteChatToServe(chatHistoryId)
      await this.queryChat()
      this.selectIndex=Math.max(0,this.selectIndex-1)
    },
    modifyChat(id,name){
      this.modifyChatVisible=true;
      this.$refs['modifyChat'].init(id,name)
    },
    handleChatNameModify(){
      this.queryChat() 
    },
    exitLogin(){
      this.$store.commit('deleteToken')
      this.$router.replace('/login')
    },
    enterEvent(event){
      if (!event.shiftKey) {
        // 如果没有按下组合键ctrl，则会阻止默认事件
        event.preventDefault();
        this.sendMessage()
      } else {
        // 如果同时按下ctrl+回车键，则会换行
        // this.inputMessage += '\n';
      }
    },
    showProblemQuestion(id){
      this.$refs['problemQuestion'].init(id)
      this.problemQuestionVisible=true
    },
    handleProblemQuestion(content){
      let temp=this.inputMessage
      this.inputMessage=content
      this.sendMessage()
      this.inputMessage=temp
    },
    // handleClose(done) {
    //     this.$confirm('确认关闭？')
    //       .then(() => {
    //         done();
    //       })
    //       .catch(() => {});
    //   },
    refreshCollections() {
      // 执行获取收藏列表的请求
      this.getCollection();
    },
    async searchProblem(){
      let res=await searchProblemFromServe(this.problemSearchInput)
      this.problemSearchInput=''
      this.problems=res.data.data
    },
    async getCommon(){
      let res = await getCommonFromServe()
      this.studentProblems=res.data.data
    },
    async getCollection(){
      console.log('11')
      let res = await getCollectionFromServe(this.studentId)
      // let res = await getCollectionFromServe(1)
      console.log(res)
     
      this.collections=res.data.data
    },
    stopGenerate(){
      stopSocket()
      this.messages[this.messages.length-1].loading=false
      this.$forceUpdate()
    },
    refreshResponse(){
      stopSocket()
      this.inputMessage=this.messages[this.messages.length-1].userChat
      this.messages.pop()
      this.sendMessage()
    },
    async editPassword(){
      this.editPasswordForm.studentId = this.studentId
      let res = await editPasswordToServer(this.editPasswordForm)
      if(res.data.code===1){
        this.$notify('修改成功')
        this.editPasswordVisible=false
      }
      else{
        this.$notify(res.data.msg)
      }
    }
  },
  async created(){
    
    await this.queryChat()
    this.studentId=this.$store.state.STUDENTID
    this.$watch(
      () => { 
        if (!this.chatHistory) return this.selectIndex
        return this.chatHistory[this.selectIndex].id 
      },
      async (newVal) => {
        console.log(newVal)
        // await this.queryChat()
        await this.getChatDetail()
      },
      {immediate:true}
    )
    await this.getCollection()
    await this.getCommon()
    
  },
  mounted(){
  },

  computed: {
    isLoading(){
      if (this.messages.length===0)return false
      return this.messages[this.messages.length - 1].loading
    },
    loadingMessage() {
      return this.$store.state.LOADINGMESSAGE
    },
    messageContext(){
      let list=[{role:'system',content:this.contextBackground}]
      for (let i = Math.max(0, this.messages.length-3);i<this.messages.length;i++){
        list.push({role:'user',content:this.messages[i].userChat})
        if (this.messages[i].aiChat.length!=0)list.push({ role: 'assistant', content: this.messages[i].aiChat })
      }
      return list
    }
  },
  watch:{
    loadingMessage(newVal){
      if(newVal.length!=0){
        this.messages[this.messages.length-1].aiChat=newVal
        this.messages[this.messages.length - 1].loading=true
        this.bottomScroll()
      }
      else setTimeout(()=>{
        this.$forceUpdate();
        this.messages[this.messages.length - 1].loading = false
      },100) 
    },
  


    historySearchInput:{
      immediate: false,
      handler(newVal) {
        clearTimeout(this.timer)

        this.timer=setTimeout(async () => {
          
          await this.queryChat()
          if(newVal===''){
            return
          }
          let ids = this.chatHistory.map((e) => {
            return e.id
          })
          let res=await searchChat(ids,newVal)
          this.chatHistory = res.data.data
          this.$forceUpdate()
        }, 300);
        
      }
    },
    problemQuestionVisible(newVal){
      if(!newVal){
        // 当问题弹窗关闭时，重新获取收藏列表
      this.refreshCollections();
      }
    },
    
  },
  components:{
    ChatSetting, ModifyChatName,PopUp,ProblemQuestion,
  }
  
}


</script>

<style>
.el-main {
  overflow: visible !important;
}

.el-button--info {
  background-color: black !important;
  border: none !important;
}

.el-button--info:hover {
  background-color: rgba(0, 0, 0, 0.6) !important;
}
.el-button--default:active,
.el-button--default:focus{
  background-color: white !important;
  color: #282828 !important;
  border-color: #333333 !important;
  border: none !important;
}
.el-button--default{
  background-color: white;
  color: #282828;
  border-color: #333333 !important;
  border: none !important;
}
.el-button--default:hover{
  background-color: rgba(255, 255, 255, 0.6) !important;
  color: #282828 !important;
  border-color: #333333 !important;
}
.el-input__inner:focus {
  border-color: white !important;
}

.el-input__inner {
  background-color: #333333 !important;
  border-radius: 18px !important;
  border: #333333 1px solid !important;
  color: white !important;
}
.el-textarea__inner{
  background-color: #333333 !important;
  border-radius: 18px !important;
  border: #333333 1px solid !important;
  color: white !important;
}
.el-textarea__inner:focus {
  border-color: white !important;
}

.el-scrollbar__wrap {
  overflow-x: hidden !important;
}

.el-button--primary{
  background-color: #000000 !important;
  border-color: #80838600 !important;
}
.el-dropdown-menu__item:focus,
.el-dropdown-menu__item:not(.is-disabled):hover{
  background-color: #b9b9b9 !important;
  color: white !important;
}
::-webkit-scrollbar{
  display: none;
}
/* 确保两个模态组件有足够的空间，不会重叠 */
/* .el-dialog {
  z-index: 2001;
}

.el-drawer {
  z-index: 2000;
} */
.collapse{
  color: white;
  font-size: 21px;
  margin: -5px;
}
.collapse:hover{
  color: gray;
  cursor: pointer;
}
.selected-chat{
  border-radius: 18px;
  background-color: #282828;
  padding:10px;
  padding-left: 15px;
  color: white;
}
.unselected-chat{
  padding: 10px;
  padding-left: 15px;
  color: white;
  cursor: pointer;
}
.avatar{
  width: 50px;height: 50px;
  background-color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;justify-content: center;
  font-size: 30px;
  margin-right: 15px;
}
.bottom-container{
  display: flex;
  height: auto;
  flex-direction: column;
  position: absolute;
  justify-content: flex-end;
  bottom: 20px;
  width: 90%;
}
.s-box{
  display: flex;
  color: white;
  align-items: center;
  margin-left: 5px;
  padding: 5px 15px 5px 8px;
  margin-top: 8px;
  cursor:pointer;
}
.s-box:hover{
  background-color: #333333;
  border-radius: 19px;
}
.text-hidden{
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}
.over-input{
  border-radius: 18px;
  background-color: #333333;
  position: absolute;
  top: 0;
  transform: translateY(-150%);
  padding: 8px 13px;
  color: white;
  z-index: 3;
  box-shadow: 0px 0.5px 15px 0px #1f1f1f;
  font-size: 15px;
  /* -moz-box-shadow: 2px 5px 30px #919191;
  -webkit-box-shadow: 2px 5px 30px #919191; */
  cursor: pointer;
}
.over-input:hover {
  background-color: #2f2f2f;
  /* color: #ababab;
  box-shadow: 2px 2px 2px black; */
}
.over-input:active{
  transform: translateY(-148%);
}
.message{
  padding:0 10px;
  background-color: #161616;
  color: white;
  border-radius: 10px;
  max-width: 80%;
  font-size: 14px;
}
.github-markdown-body{
  padding: 10px 0 0 0 !important;
  margin-bottom: 0 !important;
  font-size: 14px !important;
}
p{
  margin-bottom: 10px !important;
}
.github-markdown-body div[class*=v-md-pre-wrapper-]{
  background-color: #3f3f3fe7 !important;
  border-radius: 18px;
}
.github-markdown-body pre code,
.github-markdown-body pre tt{
  color: white !important;
}
.hljs-keyword{
  color: #8c05ff !important;
}
.roboticon{
  max-width: 40px;
  max-height: 40px;
  /* 超出等比例缩小 */
  object-fit: contain;

}
</style>