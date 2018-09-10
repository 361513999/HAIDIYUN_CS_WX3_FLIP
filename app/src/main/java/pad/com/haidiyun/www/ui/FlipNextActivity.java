package pad.com.haidiyun.www.ui;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import net.frederico.showtipsview.ShowTipsBuilder;
import net.frederico.showtipsview.ShowTipsView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hdy.upload.aidl.IUploadService;

import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.adapter.BuysAdapter;
import pad.com.haidiyun.www.adapter.FlipnNextAdapter;
import pad.com.haidiyun.www.adapter.MenusAdapter;
import pad.com.haidiyun.www.base.BaseApplication;
import pad.com.haidiyun.www.bean.BillsBean;
import pad.com.haidiyun.www.bean.DishTableBean;
import pad.com.haidiyun.www.bean.FlipBean;
import pad.com.haidiyun.www.bean.FouceBean;
import pad.com.haidiyun.www.bean.MenuBean;
import pad.com.haidiyun.www.bean.PicPoint;
import pad.com.haidiyun.www.bean.ReasonBean;
import pad.com.haidiyun.www.bean.SuitBean;
import pad.com.haidiyun.www.bean.SuitMenuBean;
import pad.com.haidiyun.www.bean.TableBean;
import pad.com.haidiyun.www.bean.TzBean;
import pad.com.haidiyun.www.common.Common;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.common.SharedUtils;
import pad.com.haidiyun.www.db.DB;
import pad.com.haidiyun.www.flip.FlipView;
import pad.com.haidiyun.www.flip.FlipView.OnFlipListener;
import pad.com.haidiyun.www.flip.FlipView.OnOverFlipListener;
import pad.com.haidiyun.www.flip.OverFlipMode;
import pad.com.haidiyun.www.inter.AreaTouch;
import pad.com.haidiyun.www.inter.BackFirst;
import pad.com.haidiyun.www.inter.BeDish;
import pad.com.haidiyun.www.inter.BuyClick;
import pad.com.haidiyun.www.inter.DishChange;
import pad.com.haidiyun.www.inter.LoadBuy;
import pad.com.haidiyun.www.inter.PicMoveT;
import pad.com.haidiyun.www.inter.ReasonI;
import pad.com.haidiyun.www.inter.Remove;
import pad.com.haidiyun.www.inter.SelectTable;
import pad.com.haidiyun.www.inter.TcT;
import pad.com.haidiyun.www.inter.UD;
import pad.com.haidiyun.www.inter.WaterConfirm;
import pad.com.haidiyun.www.net.Respon;
import pad.com.haidiyun.www.net.SocketTransceiver;
import pad.com.haidiyun.www.net.SokectUtils;
import pad.com.haidiyun.www.net.TcpClient;
import pad.com.haidiyun.www.service.B_Service;
import pad.com.haidiyun.www.service.FloatService;
import pad.com.haidiyun.www.ui.MainFragmentActivity.DataReceiver;
import pad.com.haidiyun.www.utils.Tip_Utils;
import pad.com.haidiyun.www.widget.CommonBePop;
import pad.com.haidiyun.www.widget.CommonBillsPop;
import pad.com.haidiyun.www.widget.CommonDeletePop;
import pad.com.haidiyun.www.widget.CommonDestoryPop;
import pad.com.haidiyun.www.widget.CommonGuPop;
import pad.com.haidiyun.www.widget.CommonLoadSendPop;
import pad.com.haidiyun.www.widget.CommonMenuPop;
import pad.com.haidiyun.www.widget.CommonPersonPop;
import pad.com.haidiyun.www.widget.CommonPricePop;
import pad.com.haidiyun.www.widget.CommonResPop;
import pad.com.haidiyun.www.widget.CommonRmarkPop;
import pad.com.haidiyun.www.widget.CommonSearchPop;
import pad.com.haidiyun.www.widget.CommonSendPop;
import pad.com.haidiyun.www.widget.CommonSuitPop;
import pad.com.haidiyun.www.widget.CommonTablesPop;
import pad.com.haidiyun.www.widget.CommonTipPop;
import pad.com.haidiyun.www.widget.CommonTzPop;
import pad.com.haidiyun.www.widget.NewDataToast;
import pad.com.haidiyun.www.widget.RecyclerImageView;
import pad.com.haidiyun.www.widget.UDlayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.UsbSupply;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Administrator
 */
@SuppressLint("ValidFragment")
public class FlipNextActivity extends Fragment {
	private FlipView flip_view;
	private UDlayout ud_layout;
	private FlipnNextAdapter travelAdapter;
	private LinearLayout buy_layout;
	private static FlipHandler handler;
	private Tip_Utils tipUtil;
	private Activity activity;
	private   DataReceiver dataReceiver;
	public FlipNextActivity(Activity activity){
		this.activity = activity;
	}
	public static Handler mHandler = new Handler();
	public static Runnable heartBeatRunnable = new Runnable() {
		@Override
		public void run() {
			P.c("发送数据、、、、、、、、、、、、、、、、、、、");

			handler.sendEmptyMessage(41);
			mHandler.postDelayed(this, 20000);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dataReceiver = new DataReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("app.fc.table");
		filter.addAction("app.open.table");
		filter.addAction("app.send.dish");
		filter.addAction("app.send.tui");
		filter.addAction("app.send.zeng");
		filter.addAction("app.send.table");
		activity.registerReceiver(dataReceiver, filter);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		handler = new FlipHandler(this);
		animation_viewGroup = createAnimLayout();
		sharedUtils = new SharedUtils(Common.CONFIG);
		tipUtil = new Tip_Utils();
		flip_view = (FlipView) view.findViewById(R.id.flip_view);
		buy_layout = (LinearLayout) view.findViewById(R.id.buy_layout);
		ud_layout = (UDlayout)view. findViewById(R.id.ud_layout);
		ud_layout.setUD(ud);
		// 翻转模式判断
		if (sharedUtils.getBooleanValue("isflip")) {
			flip_view.setIsFlippingVertically(true);
		} else {
			flip_view.setIsFlippingVertically(false);
		}
		flip_view.setOverFlipMode(OverFlipMode.RUBBER_BAND);
		flip_view.setCancleListener(areaTouch);
		flip_view.peakNext(true);
		flip_view.setOnFlipListener(new OnFlipListener() {
			@Override
			public void onFlippedToPage(FlipView v, int position, long id) {
				if(menu_list!=null){
					if(show_list&&menu_list.getVisibility()==View.VISIBLE){
						menu_list.setVisibility(View.VISIBLE);
					}else{
						menu_list.setVisibility(View.GONE);
					}

				}
				show_list = false;
				int INDEX = position % flipBeans.size();
				Common.LAST_PAGE = position;
				// TODO Auto-generated method stub
				NewDataToast.Text(activity,
						(INDEX + 1) + "/" + flipBeans.size(), 100).show();
				if (DB.getInstance().getCount() == 0 && !tipUtil.getWel()
						&& Common.DOING != 1) {
					// 如果是没有数据的时候,也不是第一次
					// ----------------------
					// 如果是可以点击
					P.c("OnFlipListener" + position);
					ArrayList<FouceBean> fouces = flipBeans.get(INDEX)
							.getFouceBeans();
					if (fouces.size() != 0) {
						tipUtil.setWel(true);
						showtips = new ShowTipsBuilder(activity)
								.setTarget(flip_view).setTitle("点选美食")
								.setDescription("从这里开始选择你喜欢的美食,学会了吗?")
								.setDelay(200).setListen(backFirst).build();
						showtips.showBy_XY(activity, fouces.get(0)
								.getPoints());
						showtips = null;

					}

				}
				// 当前位置
			}
		});
		flip_view.setOnOverFlipListener(new OnOverFlipListener() {

			@Override
			public void onOverFlip(FlipView v, OverFlipMode mode,
								   boolean overFlippingPrevious, float overFlipDistance,
								   float flipDistancePerPage) {
				// TODO Auto-generated method stub
				P.c("OnOverFlipListener");
				System.out.println("OnOverFlipListener");

			}
		});

		createDishLayout();
		flipBeans = new ArrayList<FlipBean>();

			/*
			 * handler.postDelayed(new Runnable() {
			 *
			 * @Override public void run() { // TODO Auto-generated method stub
			 * flip_view.smoothFlipTo(flip_view.getCurrentPage()+1);
			 * handler.postDelayed(this, 3000); } }, 3000);
			 */
		// 处理引导业务
		if (!tipUtil.getWel() && DB.getInstance().getCount() == 0
				&& !tipUtil.getBuy() && !tipUtil.getFirst()
				&& Common.DOING != 1) {
			tipUtil.setFirst(true);
			if (sharedUtils.getBooleanValue("isflip")) {
				showtips = new ShowTipsBuilder(activity)
						.setTarget(buyView).setTitle("上下翻看菜品")
						.setDescription("手指上下滑动即可翻看本店精品菜品").setDelay(200)
						.setImageSource(R.drawable.ud).setListen(backFirst)
						.build();
			} else {
				showtips = new ShowTipsBuilder(activity)
						.setTarget(buyView).setTitle("左右翻看菜品")
						.setDescription("手指左右滑动即可翻看本店精品菜品").setDelay(200)
						.setImageSource(R.drawable.lr).setListen(backFirst)
						.build();
			}
			int points[] = new int[] { 0, 0, 1, 1 };
			showtips.showBy_XY(activity, points);
		}

		// 初始化菜篮
		buys = (ListView) view.findViewById(R.id.buys);
		total = (TextView)  view.findViewById(R.id.total);
		cancle = (TextView) view. findViewById(R.id.cancle);
		destory = (TextView) view. findViewById(R.id.destory);
		send = (TextView)  view.findViewById(R.id.send);
		h_dl_tab_no_order = (TextView)  view.findViewById(R.id.h_dl_tab_no_order);
		h_dl_tab_order = (TextView) view. findViewById(R.id.h_dl_tab_order);
		qxtz = (TextView)  view.findViewById(R.id.qxtz);
		qdtz = (TextView) view. findViewById(R.id.qdtz);
		tz = (LinearLayout)  view.findViewById(R.id.tz);
		//sendTo("recy_table", "2");

		mHandler.post(heartBeatRunnable);

		dishTableBeans = new ArrayList<DishTableBean>();
		billTableBeans = new ArrayList<DishTableBean>();
		buysAdapter = new BuysAdapter(activity, dishTableBeans,
				buyClick);
		buys.setAdapter(buysAdapter);
		destory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//清空菜品信息
				//DB.getInstance()
				if(dishTableBeans.size()!=0){
					//此处采用下单成功的返回只将弹出框关闭即可
					destoryPop = new CommonDestoryPop(activity, loadBuy);
					destoryPop.showSheet();
				}else{
					NewDataToast.makeText( "还没有点菜呢");
				}
			}
		});
		cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ud_layout.smoothScrollTo(SCROOL_WIDTH, SCROOL_HEIGHT);
			}
		});
		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				P.c("点击了下单");
				if(dishTableBeans.size()!=0){
//						bePop = new CommonBePop(activity, beDish);
//						bePop.showSheet();
					P.c("------1");
					if(sharedUtils.getStringValue("billId").length()!=0){
						//NewDataToast.makeTextL( "您已经开台,如是本人请选加菜,不是本人开台请通知服务员",3000);
						//是true的话就是就餐中
						P.c("------2");
						person = sharedUtils.getIntValue("person");
						//
						//给个1是默认的
						downDish();
					}else{
						P.c("------2");
						if(sharedUtils.getStringValue("table_code").length()==0){
							NewDataToast.makeTextL( "请选确认桌台",2000);
							P.c("------4");
						}else{
							//开台操作
							P.c("------5");
							if(sharedUtils.getBooleanValue("is_waite")){
								//服务员辅助模式
								confirm.byWaiter(loginBuy);
								P.c("------6");
							}else{
								//自助模式
								if(sharedUtils.getStringValue("optName").length()==0){
									//暂时不处理
									NewDataToast.makeTextL( "没有通过服务员选择桌台",2000);
									P.c("------7");
								}else{
									P.c("------8");
									personPop  =new CommonPersonPop(activity, buyClick,null,sharedUtils.getStringValue("optName"),sharedUtils.getStringValue("optPass"));
									personPop.showSheet();
								}
							}
						}
					}
				}else{

					NewDataToast.makeText( "还没有点菜呢");
				}
			}
		});
		h_dl_tab_no_order.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				billTableBeans.clear();
				destory.setVisibility(View.VISIBLE);
				send.setVisibility(View.VISIBLE);
				tz.setVisibility(View.GONE);
				h_dl_tab_no_order.setBackgroundResource(R.drawable.h_dl_s_sel);
				h_dl_tab_order.setBackgroundResource(R.color.no_color);
				handler.sendEmptyMessage(31);
			}
		});

		h_dl_tab_order.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(loadNet()){
					tz.setVisibility(View.VISIBLE);
					h_dl_tab_no_order.setBackgroundResource(R.color.no_color);
					h_dl_tab_order.setBackgroundResource(R.drawable.h_dl_s_sel);
					destory.setVisibility(View.INVISIBLE);
					send.setVisibility(View.INVISIBLE);
				}else{
					NewDataToast.makeText( "还未就餐");
				}

			}
		});
		qxtz.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new Thread(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();
						status = -1;
						for(int i=0;i<billTableBeans.size();i++){
							billTableBeans.get(i).setZeng(false);
							billTableBeans.get(i).setTui(false);
						}
						handler.sendEmptyMessage(34);
					}
				}.start();


			}
		});
		qdtz.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//确定
				//赠送
				if(status!=-1){
					tzPop = new CommonTzPop(activity,  status,handler);
					tzPop.showSheet();
				}

			}
		});
		ud.change(true, true);
		String ip = sharedUtils.getStringValue("IP");
		String port = sharedUtils.getStringValue("port");
		/*if (ip.length() != 0) {
			gu(ip, port);
		}*/
		handler.sendEmptyMessage(3);
		Intent mServiceIntent = new Intent("com.hdy.upload.aidl");
		activity.bindService(mServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);



	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.flip_next_layout, container, false);
		return view;

	}

	private ReasonI reasonI = new ReasonI() {
		@Override
		public void select(final ArrayList<ReasonBean> beans, final DishTableBean bean) {
			// TODO Auto-generated method stub
			new Thread(){
				public void run() {
					DB.getInstance().updateDishTable(beans, bean);
					handler.sendEmptyMessage(31);
				};
			}.start();
		}

		@Override
		public void insert(final ArrayList<ReasonBean> beans, final FouceBean bean) {
			// TODO Auto-generated method stub
			// 点击菜品界面的口味
			/**
			 * 最后一个是套餐
			 */
			new Thread(){
				public void run() {
					boolean flag = DB.getInstance().addDishToPad(bean, beans, null);
					if (flag) {
						handler.sendEmptyMessage(7);
						// changeNum();
						// dishLs();
					}

				};
			}.start();

		}

		@Override
		public void init(final FouceBean bean) {
			// TODO Auto-generated method stub
			// 点击菜品界面的口味
			new Thread(){
				public void run() {
					boolean flag = DB.getInstance().addDishToPad(bean, null, null);
					if (flag) {
						handler.sendEmptyMessage(7);
						// changeNum();
						// dishLs();
					}
				};
			}.start();

		}

		@Override
		public void init(final DishTableBean bean) {
			// TODO Auto-generated method stub
			new Thread(){
				public void run() {
					DB.getInstance().updateDishInit(bean);
					handler.sendEmptyMessage(31);
				};
			}.start();
		}
	};
	private SelectTable selectTable = new SelectTable() {

		@Override
		public void select(TableBean bean, String optName,String optPass) {
			// TODO Auto-generated method stub
			if (bean != null) {
				// 设置名字，保存状态
				/*
				 * Intent intent = new Intent();
				 * intent.setAction(Common.SERVICE_ACTION);
				 * intent.putExtra("recy_table", ""); startService(intent);
				 */
				sendTo("recy_table", "");
				// 查询一次桌台
				P.c("桌号"+bean.getName()+"=="+bean.getCode());
				sharedUtils.clear("order_time");
				sharedUtils.setStringValue("table_name", bean.getName());
				if(tipView!=null){
					tipView.setText(bean.getName()+"\n"+"我的订单");
				}
				sharedUtils.setStringValue("table_code", bean.getCode());
				sharedUtils.setStringValue("optName", optName);
				sharedUtils.setStringValue("optPass", optPass);

				if (menu_item2 != null) {
					menu_item2.setText("服务:"
							+ sharedUtils.getStringValue("optName"));
				}

				//切换到最新的一页
				try {
					flip_view.flipTo(0);
				}catch (Exception e){

				}
			}
		}



		@Override
		public void isLocked() {
			// TODO Auto-generated method stub
			// 这里是不使用的接口覆盖方法
		}
	};
	private IUploadService iUploadService;
	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			iUploadService = null;

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			iUploadService = IUploadService.Stub.asInterface(service);

		}
	};

	// 进桌台
	private final int ENTER_TABLE_VAL = 1;
	// 进行多价格,多价格不选择做法，做法需要单独再选择
	private final int ENTER_PRICE_VAL = 2;
	private final int ENTER_BUY_VAL = 3;
	private final int ENTER_WAITER_ORDER_VAL = 4;
	private final int ENTER_SQ_ = 5;
	private final int TZ_DISH = 6;
	private LoadBuy oBuy = null;



	static CommonTablesPop tablesPop;
	@Override
	public void onActivityResult(int requestCode, int resultCode,
								 final Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		down();
		P.c("校验返回requestCode"+requestCode);
		if (requestCode == ENTER_TABLE_VAL && resultCode == 1000) {
			//
			down();
			if (data.getIntExtra("result", 0) == 1 && data.hasExtra("optName")) {
				if(tablesPop!=null){
					tablesPop = null;
				}
				  tablesPop = new CommonTablesPop(
						activity, selectTable,
						data.getStringExtra("optName"),data.getStringExtra("optPass"));
				tablesPop.showSheet();
			} else {
				NewDataToast.makeText("校验失败");
			}

		} else if (requestCode == ENTER_PRICE_VAL && resultCode == 1000) {
			// 时价
			if (data.hasExtra("obj")) {
				CommonPricePop pricePop = new CommonPricePop(
						activity,
						(FouceBean) data.getSerializableExtra("obj"), handler);
				pricePop.showSheet();
			}
		} else if (requestCode == ENTER_SQ_ && resultCode == 1000) {
			if (data.hasExtra("obj")) {
				// 添加菜品
				P.c("权限点菜");
				new Thread() {
					public void run() {
						P.c("加菜");
						FouceBean bean = (FouceBean) data
								.getSerializableExtra("obj");
						ArrayList<ReasonBean> resons = new ArrayList<ReasonBean>();
						Collections.sort(resons, new Comparator<ReasonBean>() {
							@Override
							public int compare(ReasonBean rb0, ReasonBean rb1) {
								String code0 = rb0.getCode();
								String code1 = rb1.getCode();
								// 可以按reasoncode对象的其他属性排序，只要属性支持compareTo方法
								return code0.compareTo(code1);
							}
						});
						P.c("点菜结构00");
						boolean flag = DB.getInstance().addDishToPad(bean,
								resons, null);
						P.c("点菜结构" + flag);
						if (flag) {
							Message msg = new Message();
							msg.what = 5;
							msg.obj = bean.getName();
							handler.sendMessage(msg);
						}
					};
				}.start();

			}

		} else if (requestCode == ENTER_BUY_VAL && resultCode == 1000) {
			// 登录确认操作
			if (oBuy != null) {
				if (data.hasExtra("optName")) {
					oBuy.success(data.getStringExtra("optName"),data.getStringExtra("optPass"));
				}
			}
			oBuy = null;
		} else if (requestCode == ENTER_WAITER_ORDER_VAL && resultCode == 1000) {
			if (oBuy != null) {
				P.c("服务员辅助点餐");
				if (data.hasExtra("optName")) {
					oBuy.waiter(data.getStringExtra("optName"),data.getStringExtra("optPass"));
				}
			}
			oBuy = null;
		}else if (requestCode ==TZ_DISH&&resultCode==1000) {
			if(data.hasExtra("optName")&&data.hasExtra("obj")&&data.hasExtra("arg1")){
				String ip = sharedUtils.getStringValue("IP");
				String port = sharedUtils.getStringValue("port");
				String dev = sharedUtils.getStringValue("dev");
				String billId = sharedUtils.getStringValue("billId");
				String userCode = data.getStringExtra("optName");
				String userPass = data.getStringExtra("optPass");
				TzBean reson = (TzBean) data.getSerializableExtra("obj");

				switch (data.getIntExtra("arg1", -1)) {
					case 1:
						//赠送
					/*try {
						JSONObject object = new JSONObject();
						object.put("method", "promo");
						object.put("device_id", dev);
						object.put("user_code", userCode);
						object.put("orderid",billId );
						object.put("authorized_pserson", userCode);
						object.put("promo_reason", reson.getCode());
						object.put("timestamp", String.valueOf(System.currentTimeMillis()));
						JSONArray array = new JSONArray();
						int k = 0;
						for(int i=0;i<billTableBeans.size();i++){
							DishTableBean ba = billTableBeans.get(i);
							if(ba.isZeng()){
								JSONObject o = new JSONObject();
								o.put("pcode", ba.getCode());
								o.put("valuation_unit", ba.getValuation_unit());
								o.put("promocount", String.valueOf(ba.getCount()));
								o.put("groupid", ba.getGroupid());
								array.put(k++, o);
							}
						}
						object.put("goods_detail" ,array);
						loadPop = new CommonLoadSendPop(activity, "赠送操作");
						loadPop.showSheet(false);
						connectTZ(ip, port, object.toString());
						P.c(object.toString());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
						DishTableBean ba0 = getZengObj();
						commonZeng(billId, ba0.getCode(), reson.getCode());


						break;
					case 0:
						//退菜
						DishTableBean ba = getTuiObj();
						if(ba!=null){
//						commonLogin(userCode,userPass,billId,ba.getCode(),reson.getCode());
							commonTui(billId,ba.getCode(),reson.getCode());
							loadPop = new CommonLoadSendPop(activity, "退菜操作");
							loadPop.showSheet(false);
					/*	try {
						 JSONObject object = new JSONObject();
							object.put("method", "cancelproduct");
							object.put("device_id", dev);
							object.put("user_code", userCode);
							object.put("orderid",billId );
							object.put("authorized_pserson", userCode);
							object.put("cancel_reason", reson.getCode());
							object.put("timestamp", String.valueOf(System.currentTimeMillis()));
							JSONArray array = new JSONArray();
							int k = 0;
							for(int i=0;i<billTableBeans.size();i++){
								DishTableBean ba = billTableBeans.get(i);
								if(ba.isTui()){
									JSONObject o = new JSONObject();
									o.put("pcode", ba.getCode());
									o.put("valuation_unit", ba.getValuation_unit());
//									o.put("promocount", String.valueOf(ba.getCount()));
									o.put("groupid", ba.getGroupid());
									array.put(k++, o);
								}
							}
							object.put("goods_detail" ,array);

							//connectTZ(ip, port, object.toString());
							//P.c(object.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
						}



						break;
					default:
						break;
				}
			}
		}
	}
	private DishTableBean getTuiObj(){
		for(int i=0;i<billTableBeans.size();i++){
			DishTableBean ba = billTableBeans.get(i);
			if(ba.isTui()){
				return ba;

			}
		}
		return null;
	}

	private DishTableBean getZengObj(){
		for(int i=0;i<billTableBeans.size();i++){
			DishTableBean ba = billTableBeans.get(i);
			if(ba.isZeng()){
				return ba;

			}
		}
		return null;
	}
	private void addItem(JSONArray array,String key,Object value) throws JSONException{
		JSONObject o1 = new JSONObject();
		o1.put("Key", key);
		o1.put("Value", value);
		array.put(o1);
	}

	private void commonTui(String bill,String code,String resoncode){
		//登录操作
		String ip = sharedUtils.getStringValue("IP");

		String dev = sharedUtils.getStringValue("dev");
		String table = sharedUtils.getStringValue("table_code");
		final JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			addItem(jsonArray, "DeviceNo", dev);
			addItem(jsonArray, "MchID", FileUtils.getDeviceId());
			addItem(jsonArray, "BillNo", bill);
			addItem(jsonArray, "ProductCD", code);
			addItem(jsonArray, "ReasonCode", resoncode);
			addItem(jsonArray, "Qty", 1);
			addItem(jsonArray, "TableNo", table);
			addItem(jsonArray,"User",sharedUtils.getStringValue("optName"));
			jsonObject.put("TC1", jsonArray);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent intent = new Intent(activity,FloatService.class);
		intent.putExtra("var", jsonObject.toString());
		activity.startService(intent);


	}
	private void commonZeng(String bill,String code,String resoncode){
		//登录操作
		String ip = sharedUtils.getStringValue("IP");

		String dev = sharedUtils.getStringValue("dev");
		String table = sharedUtils.getStringValue("table_code");
		final JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			addItem(jsonArray, "DeviceNo", dev);
			addItem(jsonArray, "MchID", FileUtils.getDeviceId());
			addItem(jsonArray, "BillNo", bill);
			addItem(jsonArray, "ProductCD", code);
			//	addItem(jsonArray, "ReasonCode", resoncode);
			addItem(jsonArray, "Qty", 1);
			addItem(jsonArray, "TableNo", table);
			addItem(jsonArray,"User",sharedUtils.getStringValue("optName"));
			jsonObject.put("ZSCP", jsonArray);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		P.c("赠送"+jsonObject.toString());
		Intent intent = new Intent(activity,FloatService.class);
		intent.putExtra("var", jsonObject.toString());
		activity.startService(intent);


	}


	// 支付
	// 查询账单
	private void connectTZ(String ip, String pt,final String json) {

		try {
			int port = Integer.parseInt(pt);
			StringBuffer buffer = new StringBuffer();
			new TcpClient() {
				@Override
				public void readTimeOut(SocketTransceiver transceiver) {
					// TODO Auto-generated method stub
				}
				@Override
				public void onReceive(SocketTransceiver transceiver,
									  String buffer) {
					// TODO Auto-generated method stub
					// 查询到价格就开始生成二维码
					P.c(buffer);
					try {
						JSONObject jsonObject = new JSONObject(buffer);
						if (jsonObject.getString("return").equals("10000")) {
							P.c("成功");
							handler.sendEmptyMessage(28);
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally{
						if(loadPop!=null){
							loadPop.cancle();
							loadPop = null;
						}
						this.disconnect();
					}
						/*Message msg = new Message();
						msg.what = 28;
						msg.obj = "赠菜成功";
						handler.sendMessage(msg);*/
				}

				@Override
				public void onDisconnect(SocketTransceiver transceiver) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onConnectFailed() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onConnect(SocketTransceiver transceiver) {
					// TODO Auto-generated method stub
					TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
					String deviceid = tm.getDeviceId();
					transceiver.send(json);

				}
			}.connect(ip, port, buffer,true);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

	}




	private CommonLoadSendPop loadPop = null;
	private WaterConfirm confirm = new WaterConfirm() {
		public void confirm(LoadBuy loginBuy) {
			oBuy = loginBuy;
			Intent intent = new Intent(activity,
					CardValActivity.class);
			startActivityForResult(intent, ENTER_BUY_VAL);
		}

		@Override
		public void byWaiter(LoadBuy loginBuy) {
			oBuy = loginBuy;
			// TODO Auto-generated method stub
			Intent intent = new Intent(activity,
					CardValActivity.class);
			startActivityForResult(intent, ENTER_WAITER_ORDER_VAL);
		};
	};

	private PicMoveT picMoveT = new PicMoveT() {
		@Override
		public void sj(FouceBean obj) {
			// TODO Auto-generated method stub
			// 时价
			Intent intent = new Intent(activity,
					CardValActivity.class);
			intent.putExtra("obj", obj);
			startActivityForResult(intent, ENTER_PRICE_VAL);
		}

		@Override
		public void rb(FouceBean obj) {
			// TODO Auto-generated method stub
			// 口味选择下单
			CommonResPop resPop = new CommonResPop(activity,
					reasonI, null, obj);
			resPop.showSheet();
		}

		@Override
		public void tc(FouceBean obj) {
			// 这里是套餐操作
			ArrayList<SuitMenuBean> suitMenuBeans = new ArrayList<SuitMenuBean>();
			// 这里是所有当前栏目的做法
			ArrayList<SuitMenuBean> rbs = DB.getInstance().getSuitMenuBeans(
					suitMenuBeans, obj.getCode());
			CommonSuitPop resPop = new CommonSuitPop(activity,
					rbs, null, null, obj);
			resPop.showSheet();
		}

		@Override
		public void sq(FouceBean obj) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(activity,
					CardValActivity.class);
			intent.putExtra("obj", obj);
			startActivityForResult(intent, ENTER_SQ_);
		}
	};
	// 套餐下单
	private TcT tcT = new TcT() {

		@Override
		public void insert(ArrayList<SuitBean> beans, FouceBean bean) {
			// TODO Auto-generated method stub

		}
	};
	/**
	 * 点击区域
	 */
	private PicPoint fliPoint = new PicPoint();
	private AreaTouch areaTouch = new AreaTouch() {
		RectF downF = new RectF();
		FouceBean bean;

		@Override
		public void down() {
			// TODO Auto-generated method stub
			// 按下操作
			if (!ud_layout.isOpen()) {
				ud_layout.smoothScrollTo(SCROOL_WIDTH, SCROOL_HEIGHT);
			}
			/*if (menu_view != null && menu_view.getVisibility() == View.GONE) {
				menu_view.setVisibility(View.VISIBLE);
			}*/
		}

		@Override
		public void click(FouceBean bn, RectF dF) {
			downF = dF;
			if (Common.guKeys.containsKey(bn.getCode())) {
				bean = null;
				NewDataToast.makeText(bn.getName() + ",已沽清");

			} else {
				bean = bn;
			}

		}

		@Override
		public void up(final float x, final float y, boolean mIsFlipping) {
			// TODO Auto-generated method stub
			if (!mIsFlipping) {
				P.c(x + "弹起" + y + "---" + mIsFlipping);
				if (!downF.isEmpty()) {
					if (downF.contains(x, y)) {
						// 包含
						// 按下和弹起在一个区域里
						/*
						 * PicPoint point = new PicPoint();
						 * fliPoint.setPoint(new int[]{(int)x,(int)y});
						 * fliPoint.setWidth(30); fliPoint.setHeight(30);
						 * setAnim(fliPoint);
						 */
						// 选择口味是否必选或者可先判断sharedUtils.getBooleanValue("isCook")

						if (bean != null) {
							// 查看是否有多口味选择
							// ----------------------------
							final ArrayList<ReasonBean> resons = new ArrayList<ReasonBean>();

							Collections.sort(resons,
									new Comparator<ReasonBean>() {
										@Override
										public int compare(ReasonBean rb0,
														   ReasonBean rb1) {
											String code0 = rb0.getCode();
											String code1 = rb1.getCode();
											// 可以按reasoncode对象的其他属性排序，只要属性支持compareTo方法
											return code0.compareTo(code1);
										}
									});

							if (bean.isPrice_modify()) {
								// 临时菜,需要确认价格才能点击
								// picMoveT.sj(bean);
								Message msg = new Message();
								msg.what = 21;
								msg.obj = bean;
								handler.sendMessage(msg);
							} else if (bean.isFree()) {
								// 授权才能点击
								P.c("有吗。。。。。。。。。。。");
								// picMoveT.sq(bean);
								Message msg = new Message();
								msg.what = 22;
								msg.obj = bean;
								handler.sendMessage(msg);
							} else {
								new Thread() {
									public void run() {
										if (DB.getInstance().isFujia(bean) != 0) {
											// 显示口味选择
											if (sharedUtils
													.getBooleanValue("isCook")) {
												// picMoveT.rb(bean);
												Message msg = new Message();
												msg.what = 23;
												msg.obj = bean;
												handler.sendMessage(msg);

											} else {
												boolean flag = DB.getInstance()
														.addDishToPad(bean,
																resons, null);
												if (flag) {
													Message msg = new Message();
													int[] fo = new int[2];
													fo[0] = (int) x;
													fo[1] = (int) y;
													msg.what = 20;
													msg.obj = fo;
													handler.sendMessage(msg);
												}
											}

										} // 是套餐
										else if (bean.isSuit()) {
											// picMoveT.tc(bean);
											Message msg = new Message();
											msg.what = 24;
											msg.obj = bean;
											handler.sendMessage(msg);

										} else {
											// 不显示口味选择，
											boolean flag = DB.getInstance()
													.addDishToPad(bean, resons,
															null);
											if (flag) {
												Message msg = new Message();
												int[] fo = new int[2];
												fo[0] = (int) x;
												fo[1] = (int) y;
												msg.what = 20;
												msg.obj = fo;
												handler.sendMessage(msg);
											}
										}
									};
								}.start();

							}

							//
							if (Common.DOING != 1 && tipUtil.getWel()
									&& DB.getInstance().getCount() == 1
									&& !tipUtil.getBuy()) {
								// 已经有欢迎了
								tipUtil.setBuy(true);
								// 证明点菜了
								// 不设置监听行为
								showtips = new ShowTipsBuilder(
										activity)
										.setTarget(buyView)
										.setTitle("查看菜品&下单")
										.setDescription(
												"点这里,可以查看当前菜品\n还可以将选择好的美食用平板直接下单")
										.setDelay(200).setListen(backFirst)
										.build();
								showtips.show(activity, true);
								showtips = null;
							}
							/*
							 * //到处飞 final ParticleSystem ps = new
							 * ParticleSystem(activity, 100,
							 * R.drawable.smial, 800); ps.setSpeedRange(0.1f,
							 * 0.25f); int []s = bean.getPoints();
							 * if(s.length==4){ ps.emit(getCenter(s[0],
							 * s[2]),getCenter(s[1], s[3]),1); } CountDownTimer
							 * timer = new CountDownTimer(800,10) {
							 *
							 * @Override public void onTick(long arg0) { // TODO
							 * Auto-generated method stub }
							 *
							 * @Override public void onFinish() { // TODO
							 * Auto-generated method stub ps.stopEmitting();
							 * ps.cancel(); } }; timer.start();
							 */
							// showDishNum();
							handler.sendEmptyMessage(4);
						}
					} else {
						// 按下和弹起不在一起区域
						System.out.println("不是");
					}
				} else {
					// 按下都没有在区域里
					System.out.println("飞了");
				}
			}
		}

		@Override
		public void init(RectF df) {
			// TODO Auto-generated method stub
			downF = df;
		}

	};
	/**
	 * 菜品改变
	 */
	private DishChange dishChange = new DishChange() {

		@Override
		public void change() {
			// TODO Auto-generated method stub
			// showDishNum();
			handler.sendEmptyMessage(4);
		}
	};
	private BackFirst backFirst = new BackFirst() {

		@Override
		public void goBack(boolean flag) {
			// TODO Auto-generated method stub
			// toBack(flag);
			if (flag) {
				// down();
			} else {
				// pause();
			}
			if (flag && !tipUtil.getBuy()) {
				// 这里就是可以正常点餐了，移动到了点击回调里面

			}
		}
	};

	private int getCenter(int i, int j) {
		return (i + j) / 2;
	}

	/**
	 * 显示菜品数量
	 */
	private void showDishNum() {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Animation shake = AnimationUtils.loadAnimation(activity,
						R.anim.buy_anim);
				buyView.startAnimation(shake);
				new Thread(){
					public void run() {
						int count = DB.getInstance().getCount();
						Message msg = new Message();
						msg.what= 29;
						msg.arg1 = count;
						handler.sendMessage(msg);
					};
				}.start();
			}
		}, 800);


	}

	private OnDismissListener cancelListener = new OnDismissListener() {

		@Override
		public void onDismiss(DialogInterface arg0) {
			// TODO Auto-generated method stub
			// showDishNum();
			handler.sendEmptyMessage(4);
			down();

		}
	};
	private TextView buyView, tipView;
	private AnimationDrawable animationDrawable;
	private Typeface tf;
	private RelativeLayout menuLayout;
	//	private LinearLayout menu_view;
	private TextView select_table, menu_item1, menu_item2;
	String TAG = "complete";
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(dataReceiver!=null){
			activity.unregisterReceiver(dataReceiver);
		}
			activity.unbindService(serviceConnection);
		 if(heartBeatRunnable!=null){
			 mHandler.removeCallbacks(heartBeatRunnable);
		 }



		ViewGroup rootView = (ViewGroup)activity.getWindow().getDecorView();
		for(int i=0;i<rootView.getChildCount();i++){
			if(rootView.getChildAt(i).getTag()!=null&&rootView.getChildAt(i).getTag().equals(TAG)){
				rootView.removeViewAt(i);
			}
		}
	}

	/**
	 * 创建餐盘
	 */

	private LinearLayout my_dish_layout,menu_lau;
	private ListView menu_list;
	private FrameLayout menu_v;
	private void createDishLayout() {
		tf = Typeface.createFromAsset(activity.getAssets(), "font/mb.ttf");
		ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView();
		// RelativeLayout animLayout = new RelativeLayout(this);
		menuLayout = (RelativeLayout) RelativeLayout.inflate(
				activity, R.layout.menu_layout, null);
		//menu_view = (LinearLayout) menuLayout.findViewById(R.id.menu_view);
		menuLayout.setTag(TAG);
		menu_v = (FrameLayout) menuLayout.findViewById(R.id.menu_v);
		menu_list = (ListView) menuLayout.findViewById(R.id.menu_list);
		final ArrayList<MenuBean> menuBeans =  DB.getInstance().getMenus();
		MenusAdapter menusAdapter = new MenusAdapter(activity, menuBeans);
		menu_list.setAdapter(menusAdapter);
		menu_list.setVisibility(View.GONE);
		menu_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int arg2, long arg3) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = 2;
				msg.arg1 = menuBeans.get(arg2).getPage();
				handler.sendMessage(msg);
			}
		});
		menu_v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(menu_list.getVisibility()==View.GONE){
					menu_list.setVisibility(View.VISIBLE);
				}else{
					menu_list.setVisibility(View.GONE);
				}
			}
		});
		menu_lau = (LinearLayout) menuLayout.findViewById(R.id.menu_lau);
		tipView = (TextView) menuLayout.findViewById(R.id.tipView);
		buyView = (TextView) menuLayout.findViewById(R.id.buyView);
		my_dish_layout = (LinearLayout) menuLayout.findViewById(R.id.my_dish_layout);
		buyView.setTypeface(tf);
		buyView.getBackground().setAlpha(200);
		//menu_view.getBackground().setAlpha(150);
		tipView.setTypeface(tf);
		if(sharedUtils.getStringValue("table_name").length()!=0){

			tipView.setText(sharedUtils.getStringValue("table_name")+"\n"+"我的订单");

		}

		// animLayout.addView(menuLayout);
		// animLayout.setBackgroundColor(R.color.red);
		// animationDrawable = (AnimationDrawable) buyView.getBackground();
		// animationDrawable.start();
		// showDishNum();
		handler.sendEmptyMessage(4);
		// Glide.with(BaseApplication.application).load(R.drawable.buy).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT).into(buyView);
		my_dish_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				/*
				 * if(buyView.getText().toString().length()==0){ //还没有点餐
				 * NewDataToast.makeText("请先点餐"); }else{
				 *
				 * }
				 */
				/*
				 * if(buysPop==null){ buysPop = new
				 * CommonBuysPop(activity, confirm, dishChange,
				 * cancelListener); buysPop.showSheet(); }
				 */
				String view = buyView.getText().toString();
				if (sharedUtils.getStringValue("billId").length() != 0
						|| view.length() != 0) {
					ud_layout.smoothScrollTo(SCROOL_WIDTH, SCROOL_HEIGHT);
				} else {
					NewDataToast.makeText("请先点餐");
				}

				//这里查询一下桌台状态
				sendTo("recy_table", "1");
			}
		});

		my_dish_layout.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		rootView.addView(menuLayout);
		// 设置进行类目和服务操作
		select_table = (TextView) menuLayout.findViewById(R.id.select_table);
		menu_item1 = (TextView) menuLayout.findViewById(R.id.menu_item1);
		menu_item2 = (TextView) menuLayout.findViewById(R.id.menu_item2);

		//
		menu_item1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CommonSearchPop searchPop = new CommonSearchPop(
						activity, handler);
				searchPop.showSheet();
			}
		});
		//
		if (sharedUtils.isHere("optName")
				&& sharedUtils.getStringValue("table_name").length() != 0) {
			menu_item2.setText("服务:" + sharedUtils.getStringValue("optName"));
		}
		select_table.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (FileUtils.db_exits()) {

					Intent intent = new Intent(activity,
							CardValActivity.class);
					startActivityForResult(intent, ENTER_TABLE_VAL);
				}
			}
		});
	}

	private void sendTo(String tip, String o) {
		P.c(tip+"发生sevice-----"+o);
		Intent intent = new Intent();
		intent.setAction(Common.SERVICE_ACTION);
		intent.putExtra(tip, o);
		activity.startService(intent);
	}

	private ShowTipsView showtips;
	private volatile ArrayList<FlipBean> flipBeans;
	private SharedUtils sharedUtils;
	private FrameLayout animation_viewGroup;
	private UD ud = new UD() {

		@Override
		public void change(boolean isOpen,boolean init) {
			// TODO Auto-generated method stub
			if(init){
				P.c("运行========");
				if (isOpen) {
					sendTo("open_buy", "1");
					menu_lau.setVisibility(View.VISIBLE);
					if(my_dish_layout!=null){
						my_dish_layout.setVisibility(View.VISIBLE);
					}
				}else{
					if(my_dish_layout!=null){
						my_dish_layout.setVisibility(View.GONE);
					}

					sendTo("open_buy", "0");
					sendTo("recy_table", "0");
					menu_lau.setVisibility(View.GONE);
					{
						destory.setVisibility(View.VISIBLE);
						send.setVisibility(View.VISIBLE);
						h_dl_tab_no_order.setBackgroundResource(R.drawable.h_dl_s_sel);
						h_dl_tab_order.setBackgroundResource(R.color.no_color);
						tz.setVisibility(View.GONE);
						billTableBeans.clear();
						status = -1;
						handler.sendEmptyMessage(31);
					}
				}
			}else{
				P.c("运行======+++");
				if(!isOpen){
					P.c("运行======+++22");
				}
			}
		}
	};



	private CommonTzPop tzPop;
	private ListView buys;
	private BuysAdapter buysAdapter;
	private Dialog dlg;
	private ArrayList<DishTableBean> dishTableBeans, billTableBeans;
	private TextView total, cancle, send, destory,h_dl_tab_no_order,h_dl_tab_order,qxtz,qdtz;
	private LinearLayout tz;
	private CommonBePop bePop;

	/**
	 * 加载网络中的菜单
	 */
	private boolean loadNet(){
		String ip = sharedUtils.getStringValue("IP");
		String pt = sharedUtils.getStringValue("port");
		String billId = sharedUtils.getStringValue("billId");
		if(billId.length()==0){
			return false;
		}
		connectSel(ip, pt);
		return true;
	}
	//加载已下单菜品

	private void connectSel(String ip,String pt){
		P.c("发送----------------------------");
		if (selClient.isConnected()) {
			// 断开连接
			P.c("断开连接----------------------------");
			selClient.disconnect();
		} else {
			try {
				int port = Integer.parseInt(pt);
				StringBuffer buffer = new StringBuffer();
				selClient.connect(ip, port,buffer,true);
				P.c("连接后台----------------------------");
			} catch (NumberFormatException e) {
				handler.sendEmptyMessage(32);
				e.printStackTrace();
			}
		}

	}
	private int  status = -1;
	private TcpClient selClient = new TcpClient() {
		@Override
		public void readTimeOut(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub

		}
		@Override
		public void onReceive(SocketTransceiver transceiver, String buffer) {
			// TODO Auto-generated method stub
			try {
				P.c(buffer);
				JSONObject jsonObject = new JSONObject(buffer);
				if(jsonObject.getString("return").equals("10000")){
					//
					JSONArray array = jsonObject.getJSONArray("goods_detail");
					int len = array.length();
					billTableBeans.clear();
					for(int i=0;i<len;i++){
						DishTableBean tableBean = new DishTableBean();
						JSONObject obj = array.getJSONObject(i);
						tableBean.setCode(obj.getString("pcode"));
						tableBean.setName(obj.getString("pname"));
						tableBean.setCount(Integer.parseInt(obj.getString("count")));
						tableBean.setPrice(Double.parseDouble(obj.getString("price")));
						tableBean.setUnit(obj.getString("unit"));
						tableBean.setGroupid(obj.getString("groupid"));
						tableBean.setValuation_unit(obj.getString("valuation_unit"));
						JSONArray oa = obj.getJSONArray("fujia_detail");
						int jen = oa.length();
						StringBuilder builder = new StringBuilder();
						for(int j=0;j<jen;j++){
							JSONObject o = oa.getJSONObject(j);
							if(j!=jen-1){
								builder.append(o.getString("fujia_name")+";");
							}else{
								builder.append(o.getString("fujia_name"));
							}
						}
						tableBean.setNotes(builder.toString());
						billTableBeans.add(tableBean);
					}
					handler.sendEmptyMessage(34);
				}

			} catch (Exception e) {
				// TODO: handle exception
				P.c("异常了吗0");
			}finally{
				if(selClient!=null&&selClient.isConnected()){
					selClient.disconnect();
				}
			}

			//旧版方式
			/*try {
				String temp[] = buffer.split(";");
				if(temp!=null&&temp.length!=0){
					billTableBeans.clear();
					for(int i=0;i<temp.length-1;i++){
						String s[] = temp[i].split("@");
						if(s.length>10){
							DishTableBean tableBean = new DishTableBean();
							tableBean.setName(s[4]);
							tableBean.setCount(Integer.parseInt(s[8]));
							tableBean.setPrice(Double.parseDouble(s[12]));
							tableBean.setUnit(s[16]);
							String[] rs0 = s[11].split("!");
							String[] rs1 = s[13].split("!");
							 StringBuilder builder = new StringBuilder();
							 if(!rs0.equals("")&&!rs1.equals("")){
									for(int j=0;j<rs0.length;j++){
										if(j!=rs0.length-1){
											builder.append(rs0[j]+rs1[j]+";");
										}else{
											builder.append(rs0[j]+rs1[j]);
										}
									}
							 }
							tableBean.setNotes(builder.toString());
							billTableBeans.add(tableBean);
						}

					}
					handler.sendEmptyMessage(34);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/


			/* */
		}
		@Override
		public void onDisconnect(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub

		}
		@Override
		public void onConnectFailed() {
			// TODO Auto-generated method stub

		}
		@Override
		public void onConnect(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub
			TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceid = tm.getDeviceId();
			SharedUtils utils = new SharedUtils(Common.CONFIG);
			String billId  = utils.getStringValue("billId");
			String table = utils.getStringValue("table_code");
			String userCode = utils.getStringValue("optName");
			//	transceiver.send("queryProduct="+FileUtils.mac()+"$"+userCode+"$"+table+"$$$"+billId+"$$0");
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("method", "querybillsdetail");
				jsonObject.put("device_id", sharedUtils.getStringValue("dev"));
				jsonObject.put("user_code",	sharedUtils.getStringValue("optName"));
				jsonObject.put("orderid", sharedUtils.getStringValue("billId"));
				jsonObject.put("tablenum",
						sharedUtils.getStringValue("table_code"));
				jsonObject.put("querytype", "1");
				transceiver.send(jsonObject.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	};

	private BeDish beDish = new BeDish() {

		@Override
		public void dish() {
			// TODO Auto-generated method stub
			if(sharedUtils.getStringValue("billId").length()!=0){
				NewDataToast.makeTextL( "您已经开台,如是本人请选加菜,不是本人开台请通知服务员",3000);
			}else{
				if(sharedUtils.getStringValue("table_code").length()==0){
					NewDataToast.makeTextL( "请选确认桌台",2000);
				}else{
					//开台操作
					if(sharedUtils.getBooleanValue("is_waite")){
						//服务员辅助模式
						confirm.byWaiter(loginBuy);
					}else{
						//自助模式
						if(sharedUtils.getStringValue("optName").length()==0){
							//暂时不处理
						}else{
							personPop  =new CommonPersonPop(activity, buyClick,null,sharedUtils.getStringValue("optName"),sharedUtils.getStringValue("optPass"));
							personPop.showSheet();
						}
					}
				}
			}
		}
		@Override
		public void add() {
			// TODO Auto-generated method stub

			if(sharedUtils.getStringValue("billId").length()!=0){
				//是true的话就是就餐中
				person = sharedUtils.getIntValue("person");
				//
				//给个1是默认的
				downDish();
			}else{
				NewDataToast.makeTextL("还没有开台,不能加菜" ,3000);
			}
		}
	};
	// 移除接口
	private Remove remove = new Remove() {

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(31);
			dishChange.change();
		}
	};
	// 选择规格
	public ReasonI markI = new ReasonI() {
		@Override
		public void init(final DishTableBean bean) {
			// TODO Auto-generated method stub

			new Thread() {
				public void run() {
					DB.getInstance().updateMarkInit(bean);
					handler.sendEmptyMessage(31);
				};
			}.start();
		}

		// 下面是在菜品添加界面上加口味
		@Override
		public void init(FouceBean bean) {
			// TODO Auto-generated method stub

		}

		@Override
		public void select(final ArrayList<ReasonBean> beans,
						   final DishTableBean bean) {
			// TODO Auto-generated method stub
			new Thread() {
				public void run() {
					DB.getInstance().updateMarkTable(beans, bean);
					handler.sendEmptyMessage(31);
				};
			}.start();
		}

		@Override
		public void insert(ArrayList<ReasonBean> beans, FouceBean bean) {
			// TODO Auto-generated method stub

		}
	};
	private int person = -1;

	private void downDish() {
		if (sharedUtils.getBooleanValue("is_waite")) {
			// 服务员确认操作

			confirm.confirm(loginBuy);

		} else {
			// 顾客自助点餐模式
			// tipPop = new CommonTipPop(context, sureBuy);
			tipPop = new CommonTipPop(activity, loginBuy);
			tipPop.showSheet();
		}
	}

	private CommonPersonPop personPop;
	private CommonSendPop sendPop;
	private CommonTipPop tipPop;
	private CommonDestoryPop destoryPop;
	// 买单成功
	private LoadBuy loadBuy = new LoadBuy() {
		@Override
		public void success(String optName,String optPass) {
			// TODO Auto-generated method stub
			dishChange.change();
			if (cancelListener != null) {
				cancelListener.onDismiss(dlg);
			}
			ud_layout.smoothScrollTo(SCROOL_WIDTH, SCROOL_HEIGHT);
		}
		@Override
		public void waiter(String optName,String optPass) {
			// TODO Auto-generated method stub
		}
	};
	private LoadBuy loginBuy = new LoadBuy() {

		@Override
		public void success(String optName,String optPass) {
			// TODO Auto-generated method stub
			// 这里进行登录成功正式下单操作
			if (person != -1) {

				if(sharedUtils.getStringValue("bills").length()!=0){
					String buffer = sharedUtils.getStringValue("bills");
					String []temps = buffer.split(",");
					ArrayList<BillsBean> bbs = new ArrayList<BillsBean>();
					for(int i=0;i<temps.length;i++){
						BillsBean bb = new BillsBean();
						String tm[] = temps[i].split("_");
						bb.setName(tm[0]);
						bb.setPerson(Integer.parseInt(tm[1]));
						bbs.add(bb);
					}

					CommonBillsPop billsPop = new CommonBillsPop(activity, handler, bbs);
					billsPop.setWhat(40);
					billsPop.putObj(new String[]{optName,optPass});
					billsPop.showSheet();
				}else{
					Message msg = new Message();
					msg.what = 40;
					msg.obj = new String[]{optName,optPass};
					handler.sendMessage(msg);
				}


				//ud_layout.smoothScrollTo(SCROOL_WIDTH, SCROOL_HEIGHT);
			} else {
				NewDataToast.makeText("就餐人数错误");
			}
		}

		@Override
		public void waiter(String optName,String optPass) {
			// TODO Auto-generated method stub
			P.c("服务员人数选择");
			personPop = new CommonPersonPop(activity, buyClick,
					loginBuy, optName,optPass);
			personPop.showSheet();
		}
	};
	private BuyClick buyClick = new BuyClick() {
		@Override
		public void add(final DishTableBean obj) {
			new Thread() {
				public void run() {
					DB.getInstance().changeNum(obj.getCount() + 1, obj.getI());
					handler.sendEmptyMessage(31);
				};
			}.start();

		}

		@Override
		public void delete(final DishTableBean obj) {
			if (obj.getCount() == 1 && obj.getCount() > 0) {
				buyClick.remove(obj);
			} else {
				new Thread() {
					public void run() {
						DB.getInstance().changeNum(obj.getCount() - 1,
								obj.getI());
						handler.sendEmptyMessage(31);
					};
				}.start();

			}
		}

		@Override
		public void remove(DishTableBean obj) {
			// TODO Auto-generated method stub
			CommonDeletePop deletePop = new CommonDeletePop(
					activity, obj, remove);
			deletePop.showSheet();
		}

		@Override
		public void res(DishTableBean obj) {
			// TODO Auto-generated method stub
			// 多口味选择

			CommonResPop resPop = new CommonResPop(activity,
					reasonI, obj, null);
			resPop.showSheet();
		}

		@Override
		public void mark(DishTableBean obj) {
			// TODO Auto-generated method stub
			//
			P.c("口味选择");
			CommonRmarkPop resPop = new CommonRmarkPop(activity,
					obj, markI);
			resPop.showSheet();
		}

		@Override
		public void person() {
			// TODO Auto-generated method stub
			// 人数选择
			person = sharedUtils.getIntValue("person");
			// 在这里进行真正的下单
			downDish();
			P.c("订单号" + sharedUtils.getStringValue("billId"));
		}

		@Override
		public void person(LoadBuy buy, String optName,String optPASS) {
			// TODO Auto-generated method stub
			person = sharedUtils.getIntValue("person");
			loginBuy.success(optName,optPASS);
		}

		@Override
		public void change(int x, ArrayList<DishTableBean> objs) {
			// TODO Auto-generated method stub
			//
			status = x;
			//
//			buysAdapter.notifyDataSetChanged();

//			billTableBeans.clear();
//			billTableBeans = objs;
			handler.sendEmptyMessage(34);
		}

	};



	private CountDownTimer backTimer;

	/**
	 * 返回到首页
	 */
	/*
	 * private void toBack(boolean start){ if(backTimer!=null){
	 * backTimer.cancel(); }else{ backTimer = new CountDownTimer(30000,100) {
	 *
	 * @Override public void onTick(long arg0) { // TODO Auto-generated method
	 * stub }
	 *
	 * @Override public void onFinish() { // TODO Auto-generated method stub
	 * handler.sendEmptyMessage(2); } }; } if(start){ backTimer.start(); }
	 *
	 * }
	 */
	/**
	 * 发送一次点击广播
	 */
	private void down() {
		Intent intent = new Intent();
		intent.setAction(Common.TOUCH_DOWN);
		activity.sendBroadcast(intent);
	}

	/**
	 * 进行一次暂停
	 */
	private void pause() {
		Intent intent = new Intent();
		intent.setAction(Common.TOUCH_PAUSE);
		activity.sendBroadcast(intent);
	}



	private void loadData() {
		// 加载数据
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				try {
					flipBeans = DB.getInstance().getDishsToFlip();
					handler.sendEmptyMessage(1);
				}catch (Exception e){
					P.c("报错了---getDishsToFlip");
				}

			}
		}.start();

	}



	class DataReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
            P.c("接收到的"+intent.getAction());
			if (intent.getAction().equals("app.data.updata")) {
				// 数据变化
				handler.sendEmptyMessage(3);
				P.c("广播通知变化");
				/*
				 * new Thread(){ public void run() { loadParse(); }; }.start();
				 */
			} else if (intent.getAction().equals("app.fc.ud")) {
				P.c("扩展操作");
				ud_layout.smoothScrollTo(SCROOL_WIDTH, SCROOL_HEIGHT);
			} else if (intent.getAction().equals("app.fc.gr")) {
				// 翻转模式
				if (flip_view.isFlippingVertically()) {
					flip_view.setIsFlippingVertically(false);
				} else {
					flip_view.setIsFlippingVertically(true);
				}
			}else if (intent.getAction().equals("app.fc.table")) {

			}else if (intent.getAction().equals("app.open.table")) {
				//

				if(intent.hasExtra("result")){
					if(intent.getBooleanExtra("result",false)){
						if(intent.getBooleanExtra("result", false)){
							P.c("开台成功");
							if(personPop!=null){
								personPop.close();
							}
							buyClick.person();
						}
					}else{

						if(intent.hasExtra("tips")){
							NewDataToast.makeText(intent.getStringExtra("tips"));
						}
						if(personPop!=null){
							personPop.close();
						}

					}
				}


			}else if (intent.getAction().equals("app.send.dish")) {
				if(intent.hasExtra("result")){
					P.c("sendPop"+(sendPop==null));
					if(sendPop!=null){
						sendPop.close();
					}
					switch (intent.getIntExtra("result", 0)) {
						case -6:
							NewDataToast.makeText( "其他设备正在操作此台位");

							break;
						case -7:
							String temp  = intent.getStringExtra("obj");
							if(temp.contains("库存不足")||temp.contains("无效的菜")){
								CommonGuPop gu = new CommonGuPop(activity, temp);
								gu.showSheet();
							}else{
								NewDataToast.makeText(temp);
							}
							break;
						case 1:
							NewDataToast.makeTextL(  "恭喜您，您已成功下单!",1500);
							loadBuy.success(null,null);
							break;
						default:
							break;
					}
				}
			}else if (intent.getAction().equals("app.send.tui")) {
				if(intent.hasExtra("obj")){
					if(loadPop!=null){
						loadPop.cancle();
						loadPop = null;
					}
					NewDataToast.makeText(intent.getStringExtra("obj"));
					//刷新菜品

					h_dl_tab_order.post(new Runnable() {
						@Override
						public void run() {
							h_dl_tab_order.performClick();
						}
					});
				//	ud_layout.smoothScrollTo(SCROOL_WIDTH, SCROOL_HEIGHT);
				}

			}else if (intent.getAction().equals("app.send.zeng")) {
				if(loadPop!=null){
					loadPop.cancle();
					loadPop = null;
				}
				NewDataToast.makeText(intent.getStringExtra("obj"));
				//ud_layout.smoothScrollTo(SCROOL_WIDTH, SCROOL_HEIGHT);
				h_dl_tab_order.post(new Runnable() {
					@Override
					public void run() {
						h_dl_tab_order.performClick();
					}
				});
			}else if(intent.getAction().equals("app.send.table")){

						if(tablesPop!=null){
							tablesPop.closeBySocket();
							tablesPop = null;
						}
			}
		}
	}

	private final int SCROOL_WIDTH = -1000;
	private final int SCROOL_HEIGHT = 0;

	/**
	 * 计算未点餐
	 *
	 * @return
	 */
	private double process() {
		// 计算总价
		int len = dishTableBeans.size();
		double total = 0;
		for (int i = 0; i < len; i++) {
			DishTableBean obj = dishTableBeans.get(i);
			total += (obj.getCount() * obj.getPrice());
		}
		return format(total);
	}

	private double format(double total) {
		BigDecimal b = new BigDecimal(total);
		// 保留2位小数
		double total_v = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return total_v;
	}
	private boolean show_list = false;
	private class FlipHandler extends Handler {
		WeakReference<FlipNextActivity> mLeakActivityRef;

		public FlipHandler(FlipNextActivity leakActivity) {
			mLeakActivityRef = new WeakReference<FlipNextActivity>(leakActivity);
		}
		@Override
		public void dispatchMessage(Message msg) {
			// TODO Auto-generated method stub
			super.dispatchMessage(msg);
			if (mLeakActivityRef.get() != null) {

				switch (msg.what) {
					case 41:
						sendTo("recy_table", "2");
						break;
					case 40:
						String optName[] = (String[]) msg.obj;
						sendPop = new CommonSendPop(activity, "正在下单",
								dishTableBeans, loadBuy, person, optName[0],optName[1],sharedUtils.getStringValue("billId"));
						sendPop.showSheet();
//					sendPop = null;
						person = -1;
						break;
					case 36:
						NewDataToast.makeText((String)msg.obj);
						ud_layout.smoothScrollTo(SCROOL_WIDTH, SCROOL_HEIGHT);
						//这里操作关闭

						break;
					case 37:
						if(loadPop!=null){
							loadPop.cancle();
							loadPop = null;
						}
						NewDataToast.makeText("登录失败");
						break;
					case 38:
						NewDataToast.makeText("退菜失败");
						break;
					case 28:
						ud_layout.smoothScrollTo(SCROOL_WIDTH, SCROOL_HEIGHT);
						NewDataToast.makeText("赠菜成功");
						break;
					case 29:
						int count = msg.arg1;
						if (count != 0) {
							buyView.setText(String.valueOf(count));
						} else {
							buyView.setText("");
						}
						break;
					case 30:
						//退增菜
						if(tzPop!=null){
							tzPop.cancle();
							tzPop = null;
						}
						Intent intent = new Intent(activity,
								CardValActivity.class);
						intent.putExtra("obj", (Serializable)msg.obj);
						intent.putExtra("arg1", msg.arg1);
						intent.putExtra("isShowmm",true);
						startActivityForResult(intent, TZ_DISH);
						break;
					case 31:
						new Thread() {
							public void run() {
								dishTableBeans.clear();
								dishTableBeans = DB.getInstance().getTableBeans();
								handler.sendEmptyMessage(32);
							};
						}.start();
						break;
					case 32:
						buysAdapter.updata(dishTableBeans, false);
						handler.sendEmptyMessage(33);
						break;
					case 33:
						total.setText("总价【未下单】:" + process());
						total.setVisibility(View.VISIBLE);
						break;
					case 34:
						P.c("已下单列表");
						buysAdapter.updata(billTableBeans,true,status);
						handler.sendEmptyMessage(35);
						break;
					case 35:
						total.setVisibility(View.INVISIBLE);
						break;
					case 20:
						int[] fo = (int[]) msg.obj;
						fliPoint.setPoint(fo);
						fliPoint.setWidth(30);
						fliPoint.setHeight(30);
						setAnim(fliPoint);
						break;
					case 21:
						FouceBean obj = (FouceBean) msg.obj;
						picMoveT.sj(obj);
						break;
					case 22:
						FouceBean obj1 = (FouceBean) msg.obj;
						picMoveT.sq(obj1);
						break;
					case 23:
						FouceBean obj2 = (FouceBean) msg.obj;
						picMoveT.rb(obj2);
						break;
					case 24:
						FouceBean obj3 = (FouceBean) msg.obj;
						picMoveT.tc(obj3);
						break;
					case 5:
						String uString = (String) msg.obj;
						Toast.makeText(activity,
								"【" + uString + "】已添加", 200).show();
					case 7:
						NewDataToast.makeTextD("已添加到点餐栏", 500);
					case 4:
						showDishNum();
						break;
					case 3:
						// 解析数据
						new Thread() {
							public void run() {
								loadData();
							};
						}.start();
						break;
					case 2:
						int index = msg.arg1;
						flip_view.flipTo(index);// 回到第一页
						show_list = true;
						break;
					case 0:
						// 用来清除动画后留下的垃圾
						try {
							animation_viewGroup.removeAllViews();
							animation_viewGroup.removeAllViewsInLayout();
							animation_viewGroup.destroyDrawingCache();
							animation_viewGroup.clearAnimation();
						} catch (Exception e) {
						}
						isClean = false;
						break;
					case 1:
						if (flipBeans.size() != 0) {
							if (travelAdapter == null) {
								travelAdapter = new FlipnNextAdapter(
										activity, flipBeans, areaTouch);
								flip_view.setAdapter(travelAdapter);
								if (flipBeans != null && flipBeans.size() != 0) {
									NewDataToast.Text(activity,
											1 + "/" + flipBeans.size(), 100).show();
								}

							} else {
								travelAdapter.updata(flipBeans);
							}
						} else {

						}
						if(flipBeans!=null&&flipBeans.size()!=0){
							flip_view.flipTo(Common.LAST_PAGE);
						}



						break;
					case -2:
						NewDataToast.Text(activity, "请检查数据完整性", -1);
						break;
					default:
						break;
				}

			}
		}
	}

	// 动画数量
	private int number = 0;
	// 清理视图
	private boolean isClean = false;
	/**
	 * 创建视图动画
	 */
	private int AnimationDuration = 800;

	/**
	 * @deprecated 将要执行动画的view 添加到动画层
	 * @param vg
	 *            动画运行的层 这里是frameLayout
	 * @param view
	 *            要运行动画的View
	 * @param location
	 *            动画的起始位置
	 * @return
	 */

	private View addViewToAnimLayout(ViewGroup vg, View view, PicPoint picPoint) {
		int location[] = picPoint.getPoint();

		int x = location[0];
		int y = location[1];

		/*
		 * FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
		 * dip2px(this,picPoint.getWidth()),dip2px(this,picPoint.getHeight()));
		 */
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				picPoint.getWidth(), picPoint.getHeight());
		lp.leftMargin = x;
		lp.topMargin = y;
		// view.setPadding(5, 5, 5, 5);
		view.setLayoutParams(lp);
		vg.addView(view);
		return view;
	}

	private void setAnim(PicPoint picPoint) {
		final RecyclerImageView iview = new RecyclerImageView(activity);

		Glide.with(BaseApplication.application).load(R.drawable.smial)
				.asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT)
				.into(iview);

		final View view = addViewToAnimLayout(animation_viewGroup, iview,
				picPoint);
		int start_location[] = picPoint.getPoint();
		Animation mScaleAnimation = new ScaleAnimation(1.5f, 0.0f, 1.5f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.1f, Animation.RELATIVE_TO_SELF,
				0.1f);
		mScaleAnimation.setDuration(AnimationDuration);
		mScaleAnimation.setFillAfter(true);

		// view.setAlpha(0.6f);

		int[] end_location = new int[2];
		buyView.getLocationInWindow(end_location);
		int endX = end_location[0] - start_location[0];
		int endY = end_location[1] - start_location[1];

		// -start_location[1]
		Animation mTranslateAnimation = new TranslateAnimation(0, endX, 0, endY);
		Animation mRotateAnimation = new RotateAnimation(0, 180,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateAnimation.setDuration(AnimationDuration);
		mTranslateAnimation.setDuration(AnimationDuration);
		AnimationSet mAnimationSet = new AnimationSet(true);

		mAnimationSet.setFillAfter(true);
		mAnimationSet.addAnimation(mRotateAnimation);
		mAnimationSet.addAnimation(mScaleAnimation);
		mAnimationSet.addAnimation(mTranslateAnimation);

		mAnimationSet.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				number++;
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub

				number--;
				if (number == 0) {
					isClean = true;
					handler.sendEmptyMessage(0);
				}

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

		});
		view.startAnimation(mAnimationSet);

	}

	/**
	 * @Description: 创建动画层
	 * @param
	 * @return void
	 * @throws
	 */
	private FrameLayout createAnimLayout() {
		ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView();
		FrameLayout animLayout = new FrameLayout(activity);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		animLayout.setLayoutParams(lp);
		animLayout.setBackgroundResource(android.R.color.transparent);
		rootView.addView(animLayout);
		return animLayout;

	}

	/**
	 * 沽清
	 */
	private void gu(String ip, String port) {
		connect(ip, port);
		P.c("查询沽清");
	}

	private void connect(String ip, String pt) {
		String dev = sharedUtils.getStringValue("dev");
		final JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			addItem(jsonArray, "DeviceNo", dev);
			jsonObject.put("GQ", jsonArray);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent intent = new Intent(activity,FloatService.class);
		intent.putExtra("var", jsonObject.toString());
		activity.startService(intent);



	/*	P.c("发送----------------------------");
		if (guClient.isConnected()) {
			// 断开连接
			P.c("断开连接----------------------------");
			guClient.disconnect();
		} else {
			try {
				int port = Integer.parseInt(pt);
				StringBuffer buffer = new StringBuffer();
				guClient.connect(ip, port,buffer);
				P.c("连接后台----------------------------");
			} catch (NumberFormatException e) {
				e.printStackTrace();
				P.c("解析异常");
			}
		}*/

	}

	TcpClient guClient = new TcpClient() {

		@Override
		public void readTimeOut(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReceive(SocketTransceiver transceiver, String buffer) {
			// TODO Auto-generated method stub
			Common.guKeys.clear();
			String str[] = buffer.split("@");
			if (str[0].equals("0")) {
				for (int i = 0; i < str.length; i++) {
					if (i != 0) {
						Common.guKeys.put(str[i], 0);
					}
				}
			}

			if (guClient != null) {
				guClient.disconnect();
			}
		}

		@Override
		public void onDisconnect(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onConnectFailed() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onConnect(SocketTransceiver transceiver) {
			// TODO Auto-generated method stub
			TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceid = tm.getDeviceId();
			String userCode = sharedUtils.getStringValue("optName");
			String dev = sharedUtils.getStringValue("dev");
			transceiver.send("soldOut=" + dev + "$" + userCode);

		}
	};

}
