package pad.com.haidiyun.www.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import pad.com.haidiyun.www.R;
import pad.com.haidiyun.www.adapter.RessonMenuAdapter;
import pad.com.haidiyun.www.bean.DishTableBean;
import pad.com.haidiyun.www.bean.FouceBean;
import pad.com.haidiyun.www.bean.ReasonBean;
import pad.com.haidiyun.www.bean.ResonMenuBean;
import pad.com.haidiyun.www.common.FileUtils;
import pad.com.haidiyun.www.common.P;
import pad.com.haidiyun.www.db.DB;
import pad.com.haidiyun.www.inter.ReasonI;
import pad.com.haidiyun.www.widget.dialog.Animations.Effectstype;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class CommonRmarkPop {
	private Context context;
	/**
	 * 下单提示
	 */
	private   IDialog dlg;
	private ReasonI markI;
	private ArrayList<ResonMenuBean> resonMenuBeans;
	private DishTableBean dishTableBean;
	private FouceBean foodsBean;
	public CommonRmarkPop(Context context,DishTableBean dishTableBean ,ReasonI markI) {
		this.context = context;
		this.dishTableBean = dishTableBean;
		this.markI = markI;
	}
	private View parent_d;
	private Handler handler = new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
				case 0:
					menuAdapter.updata(resonMenuBeans);

					if(resonMenuBeans.size()!=0){
						P.c(resonMenuBeans.size()+"口味长度");
						select(selectMenu);
					}
					break;
				case 1:
					reason_list.removeAllViews();
					reason_list.removeAllViewsInLayout();
					int count = reasonBeans.size();
					P.c("分类下面的做法"+count);
					CheckBox boxs[] = new CheckBox[count];
					CheckBox cb = null;
//				 Set<String> keys = resMap.keySet();
					Set<String> keys = resMap.containsKey(resonMenuBeans.get(selectMenu).getTag())?resMap.get(resonMenuBeans.get(selectMenu).getTag()).keySet():null;
//				 Iterator<String> it = keys.iterator();
					for(int i=0;i<count;i++){
						cb = (CheckBox) CheckBox.inflate(context, R.layout.reason_ck, null);
						LayoutParams pa = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
						pa.setMargins(2, 2, 2, 2);
//					 cb.setId((int)System.currentTimeMillis());
						cb.setLayoutParams(pa);
						ReasonBean bean = reasonBeans.get(i);
						cb.setEms( bean.getName().length()+2);
						cb.setText(bean.getName());
						cb.setTag(bean.getCode());
						cb.setTag(R.id.reason_id,bean.getPrice());
						boxs[i] = cb;
						P.c("绘制"+ boxs[i].getTag().toString());
						if(keys!=null&&keys.contains(boxs[i].getTag().toString())){
							boxs[i].setChecked(true);
						}
						/*while(it.hasNext()){
							String key = it.next();
							P.c(boxs[i].getTag().toString()+"标记:"+key);
							if(boxs[i].getTag().toString().equals(key)){
								boxs[i].setChecked(true);
							}
							key = null;
						}*/
						reason_list.addView(boxs[i]);
						cb = null;
					}
					for(int j=0;j<count;j++){
						boxs[j].setOnCheckedChangeListener(cl);
					}
					send.setEnabled(true);
					break;

				default:
					break;
			}
		};
	};
	private OnCheckedChangeListener cl = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if(arg0 instanceof CheckBox){
				ResonMenuBean rmb =   resonMenuBeans.get(selectMenu);
				if(arg1){

					ReasonBean bean = new ReasonBean();
					bean.setCode(arg0.getTag().toString());
					bean.setName(arg0.getText().toString());
					bean.setPrice(Double.parseDouble(arg0.getTag(R.id.reason_id).toString()));
					P.c("加入key"+arg0.getTag());
					bean.setTags(rmb.getTag());
					if(!resMap.containsKey(rmb.getTag())){
						Map<String, ReasonBean> rbs = new HashMap<String, ReasonBean>();
						rbs.put(arg0.getTag().toString(), bean);
						resMap.put(rmb.getTag(), rbs);
					}else{
						resMap.get(rmb.getTag()).put(arg0.getTag().toString(), bean);
						//这里不进行限制
							 /*if(rmb.getMax()>0&&resMap.containsKey(rmb.getTag())&&rmb.getMax()!=resMap.get(rmb.getTag()).size()){
								 resMap.get(rmb.getTag()).put(arg0.getTag().toString(), bean);
								}else{
									NewDataToast.makeText(rmb.getMax()+"选"+rmb.getMin());
									arg0.setChecked(false);
								}*/
					}
				}else{
					if(resMap.containsKey(rmb.getTag())){
						resMap.get(rmb.getTag()).remove(arg0.getTag().toString());
					}
				}
			}
		}
	};
	private TextView send ;
	private   AutoWrapLinearLayout reason_list;
	private ListView reason_menu;
	private RessonMenuAdapter menuAdapter;
	private int selectMenu = 0;
	private ArrayList<ReasonBean> reasonBeans;
	private void select(final int index) {
		this.selectMenu = index;
		menuAdapter.selectPosition(index);
		new Thread(){
			public void run() {
				reasonBeans = DB.getInstance().getMarkBeans(resonMenuBeans.get(index).getTag());
				handler.sendEmptyMessage(1);
			};
		}.start();
	}
	private 	Map<String, Map<String, ReasonBean>>  resMap;
	public  Dialog showSheet() {
		if(dishTableBean!=null){
			//来自菜篮子
			resMap= DB.getInstance().getSelectedMarks(dishTableBean.getI());
//			resMap= new HashMap<String, Map<String,ReasonBean>>();
		}else if(foodsBean!=null){
			//来自开始点餐
			resMap= new HashMap<String, Map<String,ReasonBean>>();
		}
		dlg = new IDialog(context, R.style.menu_pop_style);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.mark_pop_view, null);
		parent_d = layout.findViewById(R.id.parent_d);
		dlg.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface arg0) {
				// TODO Auto-generated method stub
				FileUtils.start(Effectstype.Slit, parent_d);
			}
		});
		reason_menu = (ListView) layout.findViewById(R.id.reason_menu);
		resonMenuBeans = new ArrayList<ResonMenuBean>();
		menuAdapter = new RessonMenuAdapter(context, resonMenuBeans);
		reason_menu.setAdapter(menuAdapter);
		send = (TextView) layout.findViewById(R.id.send);
		send.setEnabled(false);
		TextView cancle = (TextView) layout.findViewById(R.id.cancle);
		//---
		reason_list= (AutoWrapLinearLayout) layout.findViewById(R.id.reason_list);

		new Thread(){
			public void run() {
				resonMenuBeans.clear();
				P.c("查找");
				resonMenuBeans= DB.getInstance().getMarkMenuBeans();
				handler.sendEmptyMessage(0);
			};
		}.start();
		reason_menu.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				select(arg2);
			}
		});
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ArrayList<ReasonBean> beans = new ArrayList<ReasonBean>();
				P.c("数量"+resMap.size());
				if(resMap.size()!=0){
					Set<String> keys = resMap.keySet();
					Iterator<String> it = keys.iterator();
					while(it.hasNext()){
						String key = it.next();
						P.c(key);
						Map<String, ReasonBean> rbs = resMap.get(key);
						Set<String> rbKey = rbs.keySet();
						Iterator<String> rk = rbKey.iterator();
						while(rk.hasNext()){
							String k = rk.next();
							beans.add(rbs.get(k));
							k=null;
						}
						key = null;
					}
					if(dishTableBean!=null){
						markI.select(beans,dishTableBean);
					}else if (foodsBean!=null) {
						markI.insert(beans,foodsBean);
					}
				}else{
					if(dishTableBean!=null){
						markI.init(dishTableBean);
					}else if (foodsBean!=null) {
						markI.init(foodsBean);
					}

				}

					/*if(beans.size()!=0){
						//如果是-1那么不进行设置
						//选择了某个
						reasonI.select(beans,dishTableBean);
					}else{
						reasonI.init(dishTableBean);
//						NewDataToast.makeText(context, "请选择一份规格");
					}*/
				if(dlg!=null){
					dlg.cancel();
					dlg = null;
				}
			}
		});
		cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(dlg!=null){
					dlg.cancel();
					dlg = null;
				}
			}
		});
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}
}
