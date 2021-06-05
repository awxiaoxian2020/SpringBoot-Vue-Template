<template>
  <div>
    <div class="bg-login"></div>
    <el-row>
      <el-col :span="14">
        <div class="grid-content">
          <el-main>

          </el-main>
        </div>
      </el-col>

      <el-col :span="10">
        <transition name="el-fade-in-linear">
          <div v-show="show" class="grid-content bg-light">
            <el-main class="content-box">
              <el-form :model="form" ref="ruleForm" class="login-form">
                <h1 class="heading my-5">用户登陆</h1>
                <img src="../assets/logo.png" alt="" style="width: 100px; height: 100px;">
                <p class="paragraph">请使用电子邮箱或第三方账号登陆</p>
                <el-form-item>
                  <el-input prefix-icon="el-icon-user" placeholder="账号/邮箱" type="email" v-model="form.id" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item style="margin-bottom: 0">
                  <el-input prefix-icon="el-icon-lock" placeholder="密码" type="password" v-model="form.password" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item style="margin-bottom: 0">
                  <el-row>
                    <el-col :span="12" style="text-align: left">
                      <el-link icon="el-icon-top-left" href="#/register" :underline="false" type="info">注册账户</el-link>
                    </el-col>
                    <el-col :span="12" style="text-align: right">
                      <el-link href="#/reset" :underline="false" type="info">忘记密码?</el-link>
                    </el-col>
                  </el-row>
                </el-form-item>
                <el-form-item style="margin-bottom: 0">
                  <el-button type="primary" style="width: 80%" @click="login">登陆</el-button>
                </el-form-item>
                <el-form-item style="margin-bottom: 0">
                  <el-divider style="text-decoration-color: rgba(0,0,0,0.5)">或者</el-divider>
                </el-form-item>
                <el-form-item>
                  <el-button type="danger" style="width: 80%" icon="el-icon-lock" @click="showQQLogin">使用QQ登陆</el-button>
                </el-form-item>
                <el-form-item>
                  <el-button type="success" style="width: 80%" icon="el-icon-lock" @click="showWeChatLogin">使用微信登陆</el-button>
                </el-form-item>
              </el-form>
            </el-main>
          </div>
        </transition>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "Login",
  data () {
    return {
      show: false,
      form: {
        id: '',
        password: '',
        'remember-me': true
      }
    }
  },methods:{
    showQQLogin(){

    },showWeChatLogin(){
      this.$alert('打开微信扫一扫，扫描下方二维码进行微信登陆', '微信登陆', {
        confirmButtonText: '确定'
      });
    }, login(){
      if(this.form.id !== "" && this.form.password !== ""){
        axios.post("/auth/login", null, { params: this.form}).then(res => {
          const success = res.data.success;
          if(success){
            this.$router.push("/main")
          }else{
            this.$message.error('登陆失败，用户名或密码错误！');
          }
        }).catch(reason => {
          this.$message.error('登陆失败，网络连接错误！');
        })
      }else {
        this.$message.error('请填写用户名和密码！');
      }
    }
  }, mounted(){
    this.show = true
  }
}
</script>

<style scoped>

</style>
