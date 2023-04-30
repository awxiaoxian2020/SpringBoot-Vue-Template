import Vue from 'vue'
import Router from 'vue-router'
import Login from '../components/Login'
import Register from '../components/Register'
import ResetPassword from '../components/ResetPassword'
import Main from '../components/Main'
import DashBoard from "../components/DashBoard";
import Setting from "../components/Setting";

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      redirect: '/login'
    }, {
      path: '/login',
      name: 'Login',
      component: Login
    }, {
      path: '/register',
      name: 'Register',
      component: Register
    }, {
      path: '/reset',
      name: 'ResetPassword',
      component: ResetPassword
    }, {
      path: '/main',
      name: 'Main',
      redirect: '/main/dash',
      component: Main,
      children:[
        {
          path: 'dash',
          component: DashBoard,
        }, {
          path: 'setting',
          component: Setting,
        }
      ]
    }
  ]
})
