<template>
  <div>
    <div style="height: 300px;">
      <el-avatar class="header" :size="150" :src="user.header"></el-avatar>
      <el-image class="banner" :fit="'cover'" :src="user.background"></el-image>
      <div class="userinfo">
        <p style="font-size: 30px" v-text="user.name"></p>
        <p style="font-size: 15px" v-text="user.note"></p>
      </div>
    </div>
    <div class="card">
      <el-tabs v-model="activeName">
        <el-tab-pane label="隐私设置" name="first">
          <el-form ref="form" :model="user" label-width="80px">
            <el-row>
              <el-col :span="12">
                <el-form-item label="头像">
                  <el-avatar :src="user.header"></el-avatar>
                  <el-upload :action="uploadPath+'/user/upload/header'" :with-credentials='true' :show-file-list="false" :on-success="handleSuccess">
                    <el-button size="small" plain>更换头像</el-button>
                    <div slot="tip" style="color: lightgray; font-size: 12px">只能上传jpg/png文件，且不超过500kb</div>
                  </el-upload>
                </el-form-item>
                <el-form-item label="用户名">
                  <el-input v-model="user.name"></el-input>
                </el-form-item>
                <el-form-item label="手机号">
                  <el-input v-model="user.phone"></el-input>
                </el-form-item>
                <el-form-item label="个性签名">
                  <el-input type="textarea" :rows="5" v-model="user.note"></el-input>
                </el-form-item>
                <el-form-item style="text-align: center">
                  <el-button type="primary" @click="saveUserInfo" plain>保存修改</el-button>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="背景图片">
                  <el-image style="width: 100%; height: 400px" :fit="'cover'" :src="user.background"></el-image>
                  <el-upload :action="uploadPath+'/user/upload/background'" :with-credentials='true' :show-file-list="false" :on-success="handleSuccess">
                    <el-button size="small" plain>更换背景图片</el-button>
                    <div slot="tip" style="color: lightgray; font-size: 12px">只能上传jpg/png文件，且不超过500kb</div>
                  </el-upload>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="安全设置" name="second">
          <el-form ref="form" :model="user" label-width="80px">
            <el-row>
              <el-col :span="12">
                <el-form-item label="当前密码">
                  <el-input type="password" v-model="user.password_old"></el-input>
                </el-form-item>
                <el-form-item label="密码">
                  <el-input type="password" v-model="user.password"></el-input>
                </el-form-item>
                <el-form-item label="重复密码">
                  <el-input type="password" v-model="user.password_retype"></el-input>
                </el-form-item>
                <el-form-item style="text-align: center">
                  <el-button @click="resetPassword" type="primary" plain>重置密码</el-button>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="修改邮箱">
                  <el-input type="email" v-model="user.email"></el-input>
                </el-form-item>
                <el-row>
                  <el-col :span="16">
                    <el-form-item label="验证码">
                      <el-input v-model="user.verCode"></el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item>
                      <el-button @click="sendVer" :disabled="sendMailWait !== 0" v-text="'获取验证码'+(sendMailWait === 0 ? '' : '('+sendMailWait+')')" plain></el-button>
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-form-item style="text-align: center">
                  <el-button @click="resetMail" type="primary" plain>修改邮箱</el-button>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="偏好设置" name="third">

        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import App from "../App";

export default {
  name: "Setting",
  data(){
    return{
      user: {
        password_old: ''
      },
      activeName: 'first',
      uploadPath: axios.defaults.baseURL,
      sendMailWait: 0
    }
  },methods: {
    handleSuccess(res) {
      const success = res.success
      if(success){
        this.$message.success(res.message)
        setTimeout("location.reload()",1000);
      }else {
        this.$message.error(res.message)
      }
    }, saveUserInfo(){
      axios.post('/user/save', {
        name: this.user.name,
        note: this.user.note,
        phone: this.user.phone
      }).then(res => this.handleSuccess(res.data))
    }, sendVer(){
      const loading = this.$loading({
        lock: true,
        text: '正在请求发送邮件...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      App.methods.sendEmail('reset-email', this.user.email, m => {
        this.$message.success(m)
        loading.close()
        this.sendMailWait = 60;
        const id = setInterval(() => {
          this.sendMailWait--;
          if(this.sendMailWait <= 0) clearInterval(id)
        }, 1000)
      }, m => {
        this.$message.warning(m)
        loading.close()
      })
    }, resetMail(){
      App.methods.verEmail(this.user.verCode, m => {
        App.methods.httpPost('/auth/reset-email', this.user.email, m => {
          this.$message.success(m)
          this.user.verCode = ''
        }, m => {})
      }, this.$message.warning)
    }, resetPassword(){
      const t = /^.*(?=.{6,})(?=.*\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[\\.!@#$%^&*? ]).*$/
      if(t.test(this.user.password)) {
        if (this.user.password === this.user.password_retype) {
          App.methods.httpPost("/user/reset-password", {
            password: this.user.password,
            password_old: this.user.password_old
          }, m => {
            this.$message.success(m)
            this.user.password_old = this.user.password = this.user.password_retype = ''
          }, m => this.$message.warning(m))
        } else {
          this.$message.error('两次密码输入不一致，请检查输入！');
        }
      }else {
        this.$message.error('密码强度不符合要求，请重新输入！');
      }
    }
  }, mounted() {
    axios.get("/user/info").then(res => {
      this.user = res.data
      this.user.header = axios.defaults.baseURL+'/user/images/header'
      this.user.background = axios.defaults.baseURL+'/user/images/background'
    })
  }
}
</script>

<style scoped>
.header{
  position: relative;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  z-index: 10;
}
.banner{
  position: relative;
  top: -75px;
  width: 100%;
  height: 200px;
  border-radius: 15px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  z-index: 0;
}
.userinfo{
  position: relative;
  color: white;
  text-shadow: 3px 3px 3px rgba(0, 0, 0, 0.34);
  top: -230px;
  left: 10%;
  line-height: 20px;
  width: 80%;
}
.card{
  background-color: white;
  border-radius: 15px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  text-align: left;
  font-size: 13px;
  padding: 10px 10px 10px 10px;
}
</style>
