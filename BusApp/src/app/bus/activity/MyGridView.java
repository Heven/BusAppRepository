package app.bus.activity;

import android.widget.GridView;

public class MyGridView extends GridView
{
	public MyGridView(android.content.Context context,
			android.util.AttributeSet attrs)
	{
		super(context, attrs);
	}

	/**
	 * ���ò�����
	 */
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)  //widthMeasureSpec��heightMeasureSpec.	����������ָ���ؼ��ɻ�õĿռ��Լ���������ռ�������Ԫ����
	{
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);

	}

}