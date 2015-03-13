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
 * ��demoʹ�õ�������½�����ӱȽϼ�
 * @author �е�����
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
		ShareSDK.initSDK(this);//��ʼ��shareSdk
		setContentView(R.layout.activity_main);
		//��ʼ�� Bmob SDK
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
//	���onComplete()�������ص�����ʾ��Ȩ�ɹ��������û�����ϵͳ
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
			//����UserInfo������idΪplatform.getDb().getUserId()������
			BmobQuery<UserInfo> bmobQuery = new BmobQuery<UserInfo>();
			/*bmobQuery.getObject(this, platform.getDb().getUserId()"78a2f7f8d8", new GetListener<UserInfo>() {
			    public void onSuccess(UserInfo object) {
			    	Toast.makeText(getApplicationContext(), "���ݿ��ѯ�ɹ�", Toast.LENGTH_SHORT).show();
			    }

			    @Override
			    public void onFailure(int code, String msg) {
			    	Toast.makeText(getApplicationContext(), "���ݿ��ѯʧ��", Toast.LENGTH_SHORT).show();
			    }
			});*/
//			user.save(getApplicationContext(), new SaveListener() {
//				
//				@Override
//				public void onSuccess() {
//					Toast.makeText(getApplicationContext(), "���ݿ���ӳɹ�", Toast.LENGTH_SHORT).show();
//				}
//				
//				@Override
//				public void onFailure(int arg0, String arg1) {
//					Toast.makeText(getApplicationContext(), "���ݿ����ʧ��", Toast.LENGTH_SHORT).show();
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
			Toast.makeText(this, "�û���Ϣ�Ѵ��ڣ�������ת��¼������", Toast.LENGTH_SHORT).show();
			break;
		case 2:
			String text = getString(R.string.logining, msg.obj);
			Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
			break;
		case 3:
			Toast.makeText(this, "��Ȩ������ȡ��", Toast.LENGTH_SHORT).show();
			break;
		case 4:
			Toast.makeText(this, "��Ȩ���������������Ķ�Logcat���", Toast.LENGTH_SHORT).show();
			break;
		case 5:
			Toast.makeText(this, "��Ȩ�ɹ���������ת��¼������", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
		return false;
	}
	
	private boolean wifiState (){
		ConnectivityManager connManager = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
		if (connManager.getActiveNetworkInfo()==null) {
			Toast.makeText(getApplicationContext(), "����������", Toast.LENGTH_SHORT).show();
			return false;
		}else {
			return true;
		}	
	}
	//	�û�������������¼�¼�
	@Override
	public void onClick(View v) {
//		Platform pf =null;
		switch (v.getId()) {
		case R.id.button_main_login:
			//(1)ֱ��ʹ��QQ�˺ŵ�½
//			ConnectivityManager connManager = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
//			if (connManager.getActiveNetworkInfo()==null) {
//				Toast.makeText(getApplicationContext(), "����������", Toast.LENGTH_SHORT).show();
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
					pf2.removeAccount();//ɾ����Ȩ��Ϣ
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
					pf3.removeAccount();//ɾ����Ȩ��Ϣ
					
				}
				if (pf5.isValid()) {
					pf5.removeAccount();
				}
				Toast.makeText(this, "�˳��ɹ������������Ϸ���½��ť������������µ�½��������ɹ�", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.button_main_login_weibo:
			if (wifiState()) {
				
				// (2)�� //ʹ����Ѷ΢����½��Ҫ��ʼ����Ѷ΢��
				Platform pf = ShareSDK.getPlatform(MainActivity.this,TencentWeibo.NAME);
//				if (!pf.isValid()) {
					pf.SSOSetting(true);
					pf.setPlatformActionListener(this);
					pf.showUser(null);
//					pf.authorize();
//				}
				Log.i(TAG, "==�û�id��" + pf.getDb().getUserId() + "  �û�����:"+ pf.getDb().getUserName() + "  �û�icon��"+ pf.getDb().getUserIcon() + " �����ɶ:"+ pf.getDb().getUserGender());
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
				 // ��ȡ����ƽ̨
				/*Platform pf = ShareSDK.getPlatform(MainActivity.this,TencentWeibo.NAME);
				pf.SSOSetting(false);//�ͻ��Ȱ���Ȩ������ҳ����Ȩ
				ShareParams sp = new ShareParams();
				sp.setSite("��׿�����ֲ�");
				sp.setSiteUrl("http://zhushou.360.cn/detail/index/soft_id/2256943#next");
				pf.setPlatformActionListener(new PlatformActionListener() {
					
					@Override
					public void onError(Platform arg0, int arg1, Throwable arg2) {
						
					}
					
					@Override
					public void onComplete(Platform platform, int arg1, HashMap<String, Object> arg2) {
						System.out.println("�ɹ�");
						String str = platform.getDb().getUserId().toString();
						System.out.println("�û�ID:" + str);
						Toast.makeText(MainActivity.this, "Toast�ɹ�", Toast.LENGTH_SHORT)
								.show();
					}
					
					@Override
					public void onCancel(Platform arg0, int arg1) {
						
					}
				});
				pf.share(sp);*/
//				sp.setTitle("���Է���");
//				sp.setTitleUrl("http://www.baidu.com");
//				sp.setText("������������һ�ε�¼���÷���");
//				sp.setImageUrl("http://t0.qlogo.cn/mbloghead/3826fc0c093c44d20f7c/50");
			}
				/*String title = (String) button_main_share.getText();
//				if (ShareSDK.getPlatform(MainActivity.this,TencentWeibo.NAME).isValid()) {
					Log.i(TAG, "==����ShareSDK.getPlatform(MainActivity.this,TencentWeibo.NAME).isValid()"+ShareSDK.getPlatform(MainActivity.this,TencentWeibo.NAME).isValid());
					ShareSDK.getPlatform(MainActivity.this,TencentWeibo.NAME).setPlatformActionListener(new PlatformActionListener() {
						
						@Override
						public void onError(Platform arg0, int arg1, Throwable arg2) {
							
						}
						
						@Override
						public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
							//�ɹ�����
							Log.i(TAG, "==��ȡ�ɹ���½���û�����"+arg0.getDb().getUserName());
							//���������Ҫ����
							Platform.ShareParams share= new TencentWeibo.ShareParams();
							share.setText("������������ѧϰ�����С�������");
							share.setImageUrl("http://t0.qlogo.cn/mbloghead/3826fc0c093c44d20f7c/50");
							//����
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
//	authorize()�����������û�����Ȩҳ�������ʺ����룬Ȼ��Ŀ��ƽ̨����֤���û�(�˷����ڴ������н�����QQ�˺ŵ�½ʱ��ʹ��)
	public void authorize(Platform plat){
		if (plat.isValid()) {
			String userId =plat.getDb().getUserId();//��ȡid
			if (!TextUtils.isEmpty(userId)) {
				UIHandler.sendEmptyMessage(1, this);
				login(plat.getName(),userId,null);//������nullִ�е�½�����û�id������Ŀ��ƽ̨������֤
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
//	������Ҫ����QQ��������д�ģ�������Ǹ���ť�������¼�
	public void qq()
 {
		System.out.println("����������");
		// ��ȡ����ƽ̨
		Platform qq = ShareSDK.getPlatform(this, QQ.NAME);
		// qq.SSOSetting(false);//�ͻ��Ȱ���Ȩ������ҳ����Ȩ
		ShareParams sp = new ShareParams();
		sp.setTitle("����");
		sp.setTitleUrl("http://www.baidu.com");
		sp.setText("�����ʲô��");
		sp.setImageUrl("http://f1.sharesdk.cn/imgs/2014/05/21/oESpJ78_533x800.jpg");

		// sp.setSite("APP����");
		// sp.setSiteUrl("http://mob.com/Special/PresentedBooks");

		qq.setPlatformActionListener(new PlatformActionListener() {
			public void onError(Platform platform, int action,

			Throwable t) {
				// ����ʧ�ܵĴ������
				System.out.println("ʧ��");
			}

			public void onComplete(Platform platform, int action,
					HashMap<String, Object> res) {
				// �����ɹ��Ĵ������
				System.out.println("�ɹ�");
				String str = platform.getDb().getUserId().toString();
				System.out.println("�û�ID:" + str);
				Toast.makeText(MainActivity.this, "Toast�ɹ�", Toast.LENGTH_SHORT)
						.show();
			}

			public void onCancel(Platform platform, int action) {
				// ����ȡ���Ĵ������
				System.out.println("ȡ������");
			}
		});
		qq.share(sp);
	}

	public void TengXunWibo()
 {
		Log.i(TAG, "==����΢���������");
//		System.out.println("����������");
		// ��ȡ����ƽ̨
		Platform qq = ShareSDK.getPlatform(/*this, */TencentWeibo.NAME);
		// qq.SSOSetting(false);//�ͻ��Ȱ���Ȩ������ҳ����Ȩ
		ShareParams sp = new ShareParams();
		sp.setTitle("���Ա���ͼƬ����");
//		sp.setTitleUrl("http://t0.qlogo.cn/mbloghead/3826fc0c093c44d20f7c/50");
//		sp.setText("123���Ա���ͼƬ����");
//		sp.setImageUrl("http://f1.sharesdk.cn/imgs/2014/05/21/oESpJ78_533x800.jpg");
//		sp.setImageUrl("/mnt/sdcard/xueyi/ques_history_image/2165987image003.png");
//		sp.setImagePath("/mnt/sdcard/xueyi/ques_history_image/2165987image003.png");
//		sp.setComment("1234���Ա���ͼƬ����"+"/mnt/sdcard/xueyi/ques_history_image/2165987image003.png"+"1234���Ա���ͼƬ����"+"/mnt/sdcard/xueyi/ques_history_image/2165987image003.png");
//		 sp.setSite("��׿�����ֲ�");
//		 sp.setSiteUrl("http://android.myapp.com/myapp/detail.htm?apkName=com.qiao.learning");
		sp.setText("123");
		sp.setImagePath("/mnt/sdcard/xueyi/ques_history_image/2165987image003.png");
		sp.setText("234");
		sp.setImagePath("/mnt/sdcard/xueyi/ques_history_image/2165987image005.png");
		
		qq.setPlatformActionListener(new PlatformActionListener() {
			public void onError(Platform platform, int action,Throwable t) {
				// ����ʧ�ܵĴ������
				Log.i(TAG, "==΢���������ʧ�ܵĴ������");
			}

			public void onComplete(Platform platform, int action,
					HashMap<String, Object> res) {
				// �����ɹ��Ĵ������
				Log.i(TAG, "==΢������ɹ�");
				System.out.println("==�ɹ�");
				String str = platform.getDb().getUserId().toString();
				Log.i(TAG, "==΢�������û�ID"+str);
				System.out.println("==�û�ID:" + str);
//				Toast.makeText(MainActivity.this, "Toast�ɹ�", Toast.LENGTH_SHORT)
//						.show();
			}

			public void onCancel(Platform platform, int action) {
				// ����ȡ���Ĵ������
				Log.i(TAG, "==΢������ȡ������");
				System.out.println("==ȡ������");
			}
		});
		qq.share(sp);
	}
}
