package com.qiao.login_Main;

import java.util.HashMap;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import cn.sharesdk.tencent.weibo.TencentWeibo.ShareParams;

import com.qiao.bean.UserInfo;
import com.qiao.login_master.R;
/**
 * 此demo使用第三方登陆，例子比较简单
 * 测试git提交
 * @author 有点凉了
 *
 */
public class MainActivity extends Activity implements OnClickListener,Callback,PlatformActionListener{
	private static final String TAG="MainActivity";
	private Button button_main_login;
	private Button button_main_restart;
	private Button button_main_exit;
	private Button button_main_share;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ShareSDK.initSDK(this);//初始化shareSdk
		setContentView(R.layout.activity_main);
		//初始化 Bmob SDK
		Bmob.initialize(this, "a5e2085a1b9b81109888d6544960905b");
		button_main_login = (Button)findViewById(R.id.button_main_login);
		button_main_restart=(Button)findViewById(R.id.button_main_restart);
		button_main_share = (Button) findViewById(R.id.button_main_share);
		button_main_login.setOnClickListener(this);
		button_main_restart.setOnClickListener(this);
		button_main_share.setOnClickListener(this);
		findViewById(R.id.button_main_exit).setOnClickListener(this);
		findViewById(R.id.button_main_login_weibo).setOnClickListener(this);
		findViewById(R.id.button_main_restart_qq).setOnClickListener(this);
//		findViewById(R.id.button_main_share).setOnClickListener(this);
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ShareSDK.stopSDK(this);
	}

	@Override
	public void onCancel(Platform platform, int action) {
		if (action==Platform.ACTION_USER_INFOR) {
			UIHandler.sendEmptyMessage(3, this);
		}
	}
//	如果onComplete()方法被回调，表示授权成功，引导用户进入系统
	@Override
	public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
		if (action ==Platform.ACTION_USER_INFOR) {
			UIHandler.sendEmptyMessage(5, this);
			login(platform.getName(), platform.getDb().getUserId(), res);
			Log.i(TAG, "== platform.getName():="+platform.getName()+" platform.getDb().getUserId():="+platform.getDb().getUserId() +" platform.getDb().getUserIcon():="+platform.getDb().getUserIcon() +" platform.getDb().getUserName():="+platform.getDb().getUserName()+" platform.getDb().getUserGender():="+platform.getDb().getUserGender());
			
			UserInfo user = new UserInfo();
			user.setPlatformName(platform.getName());
			user.setUserName(platform.getDb().getUserName());
			user.setUserId(platform.getDb().getUserId());
			user.setUserIcon(platform.getDb().getUserIcon());
			user.setUserPoints(100+"");
			user.setGender(platform.getDb().getUserGender());
			//查找UserInfo表里面id为platform.getDb().getUserId()的数据
			BmobQuery<UserInfo> bmobQuery = new BmobQuery<UserInfo>();
			/*bmobQuery.getObject(this, platform.getDb().getUserId()"78a2f7f8d8", new GetListener<UserInfo>() {
			    public void onSuccess(UserInfo object) {
			    	Toast.makeText(getApplicationContext(), "数据库查询成功", Toast.LENGTH_SHORT).show();
			    }

			    @Override
			    public void onFailure(int code, String msg) {
			    	Toast.makeText(getApplicationContext(), "数据库查询失败", Toast.LENGTH_SHORT).show();
			    }
			});*/
//			user.save(getApplicationContext(), new SaveListener() {
//				
//				@Override
//				public void onSuccess() {
//					Toast.makeText(getApplicationContext(), "数据库添加成功", Toast.LENGTH_SHORT).show();
//				}
//				
//				@Override
//				public void onFailure(int arg0, String arg1) {
//					Toast.makeText(getApplicationContext(), "数据库添加失败", Toast.LENGTH_SHORT).show();
//				}
//			});
		}
	}

	@Override
	public void onError(Platform platform, int action, Throwable t) {
		if (action==Platform.ACTION_USER_INFOR) {
			UIHandler.sendEmptyMessage(4, this);
		}
		t.printStackTrace();
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case 1:
			Toast.makeText(this, "用户信息已存在，正在跳转登录操作…", Toast.LENGTH_SHORT).show();
			break;
		case 2:
			String text = getString(R.string.logining, msg.obj);
			Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
			break;
		case 3:
			Toast.makeText(this, "授权操作已取消", Toast.LENGTH_SHORT).show();
			break;
		case 4:
			Toast.makeText(this, "授权操作遇到错误，请阅读Logcat输出", Toast.LENGTH_SHORT).show();
			break;
		case 5:
			Toast.makeText(this, "授权成功，正在跳转登录操作…", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
		return false;
	}
	
	private boolean wifiState (){
		ConnectivityManager connManager = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
		if (connManager.getActiveNetworkInfo()==null) {
			Toast.makeText(getApplicationContext(), "请连接网络", Toast.LENGTH_SHORT).show();
			return false;
		}else {
			return true;
		}	
	}
	//	用户触发第三方登录事件
	@Override
	public void onClick(View v) {
//		Platform pf =null;
		switch (v.getId()) {
		case R.id.button_main_login:
			//(1)直接使用QQ账号登陆
//			ConnectivityManager connManager = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
//			if (connManager.getActiveNetworkInfo()==null) {
//				Toast.makeText(getApplicationContext(), "请连接网络", Toast.LENGTH_SHORT).show();
//			}else {
			if (wifiState()) {
				authorize(new QZone(this));// 
			}
//			}
			break;
		case R.id.button_main_restart:
			if (wifiState()) {
				Platform pf2=ShareSDK.getPlatform(MainActivity.this,TencentWeibo.NAME);
				if (pf2.isValid()) {
					pf2.removeAccount();//删除授权信息
				}
				pf2.setPlatformActionListener(this);
				pf2.authorize();
			}
			break;
		case R.id.button_main_exit:
			if (wifiState()) {
				
				Platform pf3=ShareSDK.getPlatform(MainActivity.this,TencentWeibo.NAME);
				Platform pf5=ShareSDK.getPlatform(MainActivity.this,QZone.NAME);
				if (pf3.isValid()) {
					pf3.removeAccount();//删除授权信息
					
				}
				if (pf5.isValid()) {
					pf5.removeAccount();
				}
				Toast.makeText(this, "退出成功，测试请点击上方登陆按钮，如果出现重新登陆界面则算成功", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.button_main_login_weibo:
			if (wifiState()) {
				
				// (2)、 //使用腾讯微博登陆先要初始化腾讯微博
				Platform pf = ShareSDK.getPlatform(MainActivity.this,TencentWeibo.NAME);
//				if (!pf.isValid()) {
					pf.SSOSetting(true);
					pf.setPlatformActionListener(this);
					pf.showUser(null);
//					pf.authorize();
//				}
				Log.i(TAG, "==用户id：" + pf.getDb().getUserId() + "  用户名称:"+ pf.getDb().getUserName() + "  用户icon："+ pf.getDb().getUserIcon() + " 这个是啥:"+ pf.getDb().getUserGender());
			}
			break;
		case R.id.button_main_restart_qq:
			if (wifiState()) {
				
				Platform pf4=ShareSDK.getPlatform(MainActivity.this,QZone.NAME);
				if (pf4.isValid()) {
					pf4.removeAccount();
				}
				pf4.setPlatformActionListener(this);
				authorize(new QZone(this));
			}
			break;
		case R.id.button_main_share:
			if (wifiState()) {
//				qq();
				TengXunWibo();
				 // 获取单个平台
				/*Platform pf = ShareSDK.getPlatform(MainActivity.this,TencentWeibo.NAME);
				pf.SSOSetting(false);//客户度啊授权还是网页版授权
				ShareParams sp = new ShareParams();
				sp.setSite("安卓入门手册");
				sp.setSiteUrl("http://zhushou.360.cn/detail/index/soft_id/2256943#next");
				pf.setPlatformActionListener(new PlatformActionListener() {
					
					@Override
					public void onError(Platform arg0, int arg1, Throwable arg2) {
						
					}
					
					@Override
					public void onComplete(Platform platform, int arg1, HashMap<String, Object> arg2) {
						System.out.println("成功");
						String str = platform.getDb().getUserId().toString();
						System.out.println("用户ID:" + str);
						Toast.makeText(MainActivity.this, "Toast成功", Toast.LENGTH_SHORT)
								.show();
					}
					
					@Override
					public void onCancel(Platform arg0, int arg1) {
						
					}
				});
				pf.share(sp);*/
//				sp.setTitle("测试分享");
//				sp.setTitleUrl("http://www.baidu.com");
//				sp.setText("。。。。。。一次登录永久分享");
//				sp.setImageUrl("http://t0.qlogo.cn/mbloghead/3826fc0c093c44d20f7c/50");
			}
				/*String title = (String) button_main_share.getText();
//				if (ShareSDK.getPlatform(MainActivity.this,TencentWeibo.NAME).isValid()) {
					Log.i(TAG, "==分享ShareSDK.getPlatform(MainActivity.this,TencentWeibo.NAME).isValid()"+ShareSDK.getPlatform(MainActivity.this,TencentWeibo.NAME).isValid());
					ShareSDK.getPlatform(MainActivity.this,TencentWeibo.NAME).setPlatformActionListener(new PlatformActionListener() {
						
						@Override
						public void onError(Platform arg0, int arg1, Throwable arg2) {
							
						}
						
						@Override
						public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
							//成功处理
							Log.i(TAG, "==获取成功登陆的用户名字"+arg0.getDb().getUserName());
							//分享操作需要数据
							Platform.ShareParams share= new TencentWeibo.ShareParams();
							share.setText("今天天气不错，学习分享中。。。。");
							share.setImageUrl("http://t0.qlogo.cn/mbloghead/3826fc0c093c44d20f7c/50");
							//分享
							ShareSDK.getPlatform(MainActivity.this,TencentWeibo.NAME).share(share);
						}
						
						@Override
						public void onCancel(Platform arg0, int arg1) {
							
						}
					});
				}*/
//			}
			break;
		default:
			break;
		}
	}
//	authorize()方法将引导用户在授权页面输入帐号密码，然后目标平台将验证此用户(此方法在此例子中仅仅是QQ账号登陆时候使用)
	public void authorize(Platform plat){
		if (plat.isValid()) {
			String userId =plat.getDb().getUserId();//获取id
			if (!TextUtils.isEmpty(userId)) {
				UIHandler.sendEmptyMessage(1, this);
				login(plat.getName(),userId,null);//不等于null执行登陆，讲用户id发送至目标平台进行验证
				return;
			}
		}
		plat.setPlatformActionListener(this);
		plat.SSOSetting(true);
		plat.showUser(null);
	}
	public void login(String plat,String userId,HashMap<String, Object> userInfo){
		Message msg = new Message();
		msg.what = 2;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
		
		
		
		
	}
//	比如您要分享QQ，是这样写的，我这个是个按钮触发的事件
	public void qq()
 {
		System.out.println("进入分享操作");
		// 获取单个平台
		Platform qq = ShareSDK.getPlatform(this, QQ.NAME);
		// qq.SSOSetting(false);//客户度啊授权还是网页版授权
		ShareParams sp = new ShareParams();
		sp.setTitle("标题");
		sp.setTitleUrl("http://www.baidu.com");
		sp.setText("分享的什么啊");
		sp.setImageUrl("http://f1.sharesdk.cn/imgs/2014/05/21/oESpJ78_533x800.jpg");

		// sp.setSite("APP名字");
		// sp.setSiteUrl("http://mob.com/Special/PresentedBooks");

		qq.setPlatformActionListener(new PlatformActionListener() {
			public void onError(Platform platform, int action,

			Throwable t) {
				// 操作失败的处理代码
				System.out.println("失败");
			}

			public void onComplete(Platform platform, int action,
					HashMap<String, Object> res) {
				// 操作成功的处理代码
				System.out.println("成功");
				String str = platform.getDb().getUserId().toString();
				System.out.println("用户ID:" + str);
				Toast.makeText(MainActivity.this, "Toast成功", Toast.LENGTH_SHORT)
						.show();
			}

			public void onCancel(Platform platform, int action) {
				// 操作取消的处理代码
				System.out.println("取消操作");
			}
		});
		qq.share(sp);
	}

	public void TengXunWibo()
 {
		Log.i(TAG, "==进入微博分享操作");
//		System.out.println("进入分享操作");
		// 获取单个平台
		Platform qq = ShareSDK.getPlatform(/*this, */TencentWeibo.NAME);
		// qq.SSOSetting(false);//客户度啊授权还是网页版授权
		ShareParams sp = new ShareParams();
		sp.setTitle("测试本地图片分享");
//		sp.setTitleUrl("http://t0.qlogo.cn/mbloghead/3826fc0c093c44d20f7c/50");
//		sp.setText("123测试本地图片分享");
//		sp.setImageUrl("http://f1.sharesdk.cn/imgs/2014/05/21/oESpJ78_533x800.jpg");
//		sp.setImageUrl("/mnt/sdcard/xueyi/ques_history_image/2165987image003.png");
//		sp.setImagePath("/mnt/sdcard/xueyi/ques_history_image/2165987image003.png");
//		sp.setComment("1234测试本地图片分享"+"/mnt/sdcard/xueyi/ques_history_image/2165987image003.png"+"1234测试本地图片分享"+"/mnt/sdcard/xueyi/ques_history_image/2165987image003.png");
//		 sp.setSite("安卓入门手册");
//		 sp.setSiteUrl("http://android.myapp.com/myapp/detail.htm?apkName=com.qiao.learning");
		sp.setText("123");
		sp.setImagePath("/mnt/sdcard/xueyi/ques_history_image/2165987image003.png");
		sp.setText("234");
		sp.setImagePath("/mnt/sdcard/xueyi/ques_history_image/2165987image005.png");
		
		qq.setPlatformActionListener(new PlatformActionListener() {
			public void onError(Platform platform, int action,Throwable t) {
				// 操作失败的处理代码
				Log.i(TAG, "==微博分享操作失败的处理代码");
			}

			public void onComplete(Platform platform, int action,
					HashMap<String, Object> res) {
				// 操作成功的处理代码
				Log.i(TAG, "==微博分享成功");
				System.out.println("==成功");
				String str = platform.getDb().getUserId().toString();
				Log.i(TAG, "==微博分享用户ID"+str);
				System.out.println("==用户ID:" + str);
//				Toast.makeText(MainActivity.this, "Toast成功", Toast.LENGTH_SHORT)
//						.show();
			}

			public void onCancel(Platform platform, int action) {
				// 操作取消的处理代码
				Log.i(TAG, "==微博分享取消操作");
				System.out.println("==取消操作");
			}
		});
		qq.share(sp);
	}
}
