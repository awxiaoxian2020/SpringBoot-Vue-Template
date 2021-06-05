<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: 'App',
  methods:{
    sendEmail(type, email, success, failure){
      const t = /^[A-Za-zd0-9]+([-_.][A-Za-zd]+)*@([A-Za-zd0-9]+[-.])+[A-Za-zd]{2,5}$/
      if(!t.test(email)){
        this.$message.error('请填写正确的邮箱地址！');
      }else {
        this.httpPost("/auth/mail-send-code", {email: email, type: type}, success, failure)
      }
    }, verEmail(code, success, failure){
      this.httpPost("/auth/ver-code", {code: code}, success, failure)
    }, httpPost(url, data, success, failure){
      axios.post(url, null, {params: data}).then(res => {
        const suc = res.data.success;
        const message = res.data.message;
        if(suc){
          success(message)
        }else {
          failure(message)
        }
      })
    }
  }
}
</script>

<style>
body{
  font-family: "Helvetica Neue",Helvetica,"PingFang SC","Hiragino Sans GB","Microsoft YaHei","微软雅黑",Arial,sans-serif;
  height: 100vh;
  margin: 0 0 0 0;
}
::-webkit-scrollbar-thumb {
  display: none;
}
.bg-login {
  background: url("./assets/login.jpeg") no-repeat;
  background-size: 100% 100%;
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}
#app {
  height: 100%;
}
.bg-light {
  background: rgba(255, 255, 255, 0.9);
}
.grid-content {
  text-align: center;
  display: block;
  height: 100vh;
}
.content-box {
  text-align: center;
}
.login-form {
  width: 80%;
  max-width: 300px;
  display: inline-block;
}
</style>
