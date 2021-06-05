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
              <el-page-header @back="goBack" content="重置密码"></el-page-header>
              <el-steps :active="step" align-center style="margin-top: 10%">
                <el-step title="邮箱找回"></el-step>
                <el-step title="验证邮箱"></el-step>
                <el-step title="重置密码"></el-step>
                <el-step title="完成"></el-step>
              </el-steps>

              <div class="login-form" style="margin-top: 30px">
                <el-form v-if="step === 1" :model="form" style="max-width: 300px">
                  <el-form-item>
                    <h1 class="heading my-5">通过邮箱找回</h1>
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
                    <h1 class="heading my-5">重置密码</h1>
                    <p style="max-width: 500px" class="paragraph">请输入您的新密码</p>
                  </el-form-item>
                  <el-form-item>
                    <el-input prefix-icon="el-icon-lock" placeholder="密码" type="password" v-model="form.password" autocomplete="off"></el-input>
                  </el-form-item>
                  <el-form-item>
                    <el-input prefix-icon="el-icon-lock" placeholder="重复密码" type="password" v-model="form.password_retype" autocomplete="off"></el-input>
                  </el-form-item>
                  <el-form-item style="margin-bottom: 0">
                    <el-button type="primary" style="width: 50%" @click="resetPwd">注册账户</el-button>
                  </el-form-item>
                </el-form>

                <el-form v-if="step === 4" style="max-width: 300px">
                  <el-form-item>
                    <h1 class="heading my-5"><i class="el-icon-check"></i> 完成</h1>
                    <p style="max-width: 500px" class="paragraph">密码重置完成！点击下方按钮即可进入系统。</p>
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
  name: "ResetPassword",
  data () {
    return {
      show: false,
      step: 1,
      form: {
        email: '',
        password: '',
        password_retype: '',
        verCode: ''
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
      App.methods.sendEmail('reset-password', this.form.email, m => {
        loading.close()
        this.step++;
        this.$message.success(m)
      }, m => {
        loading.close()
        this.$message.warning(m)
      })
    }, verEmail(){
      App.methods.verEmail(this.form.verCode, m => {
        this.step++;
        this.$message.success(m)
      }, m => this.$message.warning(m))
    }, resetPwd(){
      const t = /^.*(?=.{6,})(?=.*\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[\\.!@#$%^&*? ]).*$/
      if(t.test(this.form.password)) {
        if (this.form.password === this.form.password_retype) {
          App.methods.httpPost("/auth/reset-password", {password: this.form.password}, m => {
            this.step++;
            this.$message.success(m)
          }, m => this.$message.warning(m))
        } else {
          this.$message.error('两次密码输入不一致，请检查输入！');
        }
      }
    }, finish(){
      this.goBack()
    }
  }, mounted(){
    this.show = true
  }
}
</script>
