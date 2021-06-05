<template>
  <div>
    <div class="bg-login"></div>
    <el-row>
      <el-col :span="12">
        <div class="grid-content">
          <el-main>

          </el-main>
        </div>
      </el-col>
      <el-col :span="12">
        <transition name="el-fade-in-linear">
          <div v-show="show" class="grid-content bg-light">
            <el-main class="content-box">
              <el-page-header @back="goBack" content="注册账户"></el-page-header>
              <el-steps :active="step" align-center style="margin-top: 10%">
                <el-step title="填写邮箱"></el-step>
                <el-step title="验证邮箱"></el-step>
                <el-step title="填写基本信息"></el-step>
                <el-step title="完成"></el-step>
              </el-steps>

              <div class="login-form" style="margin-top: 30px">
                <el-form v-if="step === 1" :model="form" style="max-width: 300px">
                  <el-form-item>
                    <h1 class="heading my-5">通过邮箱注册</h1>
                  </el-form-item>
                  <el-form-item>
                    <el-input prefix-icon="el-icon-message" placeholder="邮箱" type="email" v-model="form.email" autocomplete="off"></el-input>
                  </el-form-item>
                  <el-form-item style="margin-bottom: 0">
                    <el-button type="primary" style="width: 50%" @click="sendEmail">获取验证码</el-button>
                  </el-form-item>
                </el-form>

                <el-form v-if="step === 2" :model="form" style="max-width: 300px">
                  <el-form-item>
                    <h1 class="heading my-5">验证邮箱</h1>
                    <p class="paragraph" v-text="'我们向 '+form.email+' 发送了一封带有验证码的邮件，请注意查收！'"></p>
                  </el-form-item>
                  <el-form-item>
                    <el-input prefix-icon="el-icon-chat-dot-square" placeholder="验证码" type="email" v-model="form.verCode" autocomplete="off"></el-input>
                  </el-form-item>
                  <el-form-item style="margin-bottom: 0">
                    <el-button type="primary" style="width: 50%" @click="verEmail">验证邮箱</el-button>
                  </el-form-item>
                </el-form>

                <el-form v-if="step === 3" :model="form" style="max-width: 300px">
                  <el-form-item>
                    <h1 class="heading my-5">填写基本信息</h1>
                    <p style="max-width: 500px" class="paragraph">请填写注册需要的用户信息</p>
                  </el-form-item>
                  <el-form-item>
                    <el-input prefix-icon="el-icon-user" placeholder="用户名" type="text" v-model="form.username" autocomplete="off"></el-input>
                  </el-form-item>
                  <el-form-item>
                    <el-popover placement="right" title="密码" width="200" trigger="hover" content="密码最少6位，包括至少1个大写字母，1个小写字母，1个数字，1个特殊字符。">
                      <el-input prefix-icon="el-icon-lock" placeholder="密码" type="password" v-model="form.password" autocomplete="off" slot="reference" ></el-input>
                    </el-popover>
                  </el-form-item>
                  <el-form-item>
                    <el-input prefix-icon="el-icon-lock" placeholder="重复密码" type="password" v-model="form.password_retype" autocomplete="off"></el-input>
                  </el-form-item>
                  <el-form-item style="margin-bottom: 0">
                    <el-button type="primary" style="width: 50%" @click="sendUserInfo">注册账户</el-button>
                  </el-form-item>
                </el-form>

                <el-form v-if="step === 4" style="max-width: 300px">
                  <el-form-item>
                    <h1 class="heading my-5"><i class="el-icon-check"></i> 完成</h1>
                    <p style="max-width: 500px" class="paragraph">账号注册成功！点击下方按钮即可进入系统。</p>
                  </el-form-item>
                  <el-form-item style="margin-bottom: 0">
                    <el-button type="primary" style="width: 50%" @click="finish">登陆系统</el-button>
                  </el-form-item>
                </el-form>
              </div>
            </el-main>
          </div>
        </transition>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import App from "../App";

export default {
  name: "Register",
  data () {
    return {
      show: false,
      step: 1,
      form: {
        email: '',
        verCode: '',
        username: '',
        password: '',
        password_retype: ''
      }
    }
  },methods: {
    goBack() {
      this.$router.push('/login')
    }, sendEmail(){
      const loading = this.$loading({
        lock: true,
        text: '正在请求发送邮件...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      App.methods.sendEmail('register', this.form.email, m => {
        this.step++;
        this.$message.success(m)
        loading.close()
      }, m => {
        this.$message.warning(m)
        loading.close()
      })
    }, verEmail(){
      App.methods.verEmail(this.form.verCode, m => {
        this.step++;
        this.$message.success(m)
      }, m => this.$message.warning(m))
    }, sendUserInfo(){
      const t = /^.*(?=.{6,})(?=.*\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[\\.!@#$%^&*? ]).*$/
      if(t.test(this.form.password)){
        if(this.form.password === this.form.password_retype){
          if(this.form.username.length > 0){
            App.methods.httpPost("/auth/register", {
              name: this.form.username,
              password: this.form.password
            }, m => {
              this.step++;
              this.$message.success(m)
            }, m => this.$message.warning(m))
          }else {
            this.$message.error('请输入用户名！');
          }
        }else {
          this.$message.error('两次密码输入不一致，请检查输入！');
        }
      }else {
        this.$message.error('密码强度不符合要求，请重新输入！');
      }
    }, finish(){
      this.goBack()
    }
  }, mounted(){
    this.show = true
  }
}
</script>
