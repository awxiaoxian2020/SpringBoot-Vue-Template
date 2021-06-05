<template>
  <div>
    <el-container style="height: 100vh; background-color: #ecf0f5">
      <el-header height="80px">
        <div class="bar">
          <div class="nav-bar">
            <el-row>
              <el-col :span="10" style=" padding: 10px 10px 10px 10px;">
                <el-button round type="danger" icon="el-icon-back" @click="logout">退出系统</el-button>
              </el-col>
              <el-col :span="4" style="text-align: center; padding: 10px 10px 10px 10px;">
                <el-input placeholder="请输入内容" prefix-icon="el-icon-search"></el-input>
              </el-col>
              <el-col :span="10" style="padding: 10px 10px 10px 10px;">
                <el-dropdown style="float: right">
                  <el-avatar :src="user.header"></el-avatar>
                  <el-dropdown-menu slot="dropdown" style="width: 200px">
                    <el-dropdown-item disabled v-text="user.name" style="font-size: 18px" icon="el-icon-user-solid"></el-dropdown-item>
                    <el-dropdown-item icon="el-icon-edit">个人信息</el-dropdown-item>
                    <el-dropdown-item @click.native="tab('3', 3)" icon="el-icon-set-up" divided>偏好设置</el-dropdown-item>
                    <el-dropdown-item @click.native="logout" icon="el-icon-switch-button">退出账户</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
                <el-badge style="float: right; margin-right: 20px"  :value="3" class="item">
                  <el-button type="warning" icon="el-icon-message" circle></el-button>
                </el-badge>
              </el-col>
            </el-row>
          </div>
        </div>
      </el-header>
      <el-container style="overflow: hidden">
        <el-aside width="300px">
          <div class="bar">
            <el-menu style="height: 100%;width: auto;overflow-y: auto" default-active="1" class="el-menu-vertical-demo" @select="tab">
              <el-menu-item index="1">
                <i class="el-icon-menu"></i>
                <span slot="title">仪表盘</span>
              </el-menu-item>
              <el-menu-item index="2">
                <i class="el-icon-document"></i>
                <span slot="title">日历</span>
              </el-menu-item>
              <el-menu-item index="3">
                <i class="el-icon-setting"></i>
                <span slot="title">个人设置</span>
              </el-menu-item>
            </el-menu>
          </div>
        </el-aside>
        <el-main>
          <div style="padding: 0 10px 0 0; height: calc(100% - 10px); border-radius: 10px; overflow-y: scroll">
            <router-view></router-view>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "Main",
  data(){
    return {
      user: {}
    }
  }, methods:{
    logout(){
      axios.post("/auth/logout").then(res => {
        const success = res.data.success;
        if(success){
          this.$router.push("/login")
        }
      }).catch(reason => {
        this.$message.error('退出失败，网络连接错误！');
      })
    }, tab(index, indexPath){
      switch (index){
        case "1":
          this.$router.push("/main/dash")
          break;
        case "2":
          this.$router.push("/main/dash")
          break;
        case "3":
          this.$router.push("/main/setting")
          break;
          default:
            alert(index)
      }
    }
  }, beforeCreate() {
    const loading = this.$loading({
      lock: true,
      text: '请稍后...',
      spinner: 'el-icon-loading',
      background: 'rgba(0, 0, 0, 0.7)'
    });
    axios.get("/user/info").then(res => {
      this.user = res.data
      this.user.header = axios.defaults.baseURL+'/user/images/header'
      this.user.background = axios.defaults.baseURL+'/user/images/background'
      loading.close()
    })
  }
}
</script>

<style scoped>
.nav-bar{
  width: 100%;
}
.bar{
  background-color: white;
  width: auto;
  height: 100%;
  border-radius: 15px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
  text-align: left;
}
.el-header{
  padding: 10px 10px 10px 10px;
  color: #333;
  text-align: center;
}
.el-aside {
  padding: 0 10px 10px 10px;
  color: #333;
  text-align: center;
  overflow: auto;
  height: 100%;
  max-height: calc(100vh - 80px);
}
.el-main {
  color: #333;
  text-align: center;
  padding: 0 0 0 0;
}
</style>
