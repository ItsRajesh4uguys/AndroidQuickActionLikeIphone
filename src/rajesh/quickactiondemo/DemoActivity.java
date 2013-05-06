package rajesh.quickactiondemo;
import rajesh.quickactiondemo.widget.QuickActionView;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

public class DemoActivity extends Activity {

	static Context context;
	static QuickActionView qa;
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.main );
		context=this;
	}

	public void onButtonClick( View v ) {
		
		// create the quick action view, passing the view anchor
		 qa= QuickActionView.Builder( v );
		
		// set the adapter
		qa.setAdapter( new CustomAdapter( this ) );
		// set the number of columns ( setting -1 for auto )
		qa.setNumColumns(1);
		// finally show the view
		qa.show();
		
	}

	/**
	 * Custom Adapter just for custom values
	 * 
	 *
	 */
	static class CustomAdapter extends BaseAdapter {	
		LayoutInflater mLayoutInflater;	
		public CustomAdapter( Context context ) {
			mLayoutInflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			
		}
		

		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
		@Override
		public long getItemId( int arg0 ) {
			return arg0;
		}

		@Override
		public View getView( int position, View arg1, ViewGroup arg2 ) {
			View view = mLayoutInflater.inflate( R.layout.action_item, arg2, false );
			Button image = (Button) view.findViewById( R.id.imgview_cancel);
			
			image.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					Toast.makeText(context, "test", 1000).show();
					/*QuickActionView qa = QuickActionView.Builder( v );				
					
					qa.dismiss();
					qa.cancel();*/
					
					qa.dismiss();
					
					
				}
			});

			return view;
		}



	};
}