/**
 * QuickActionView.java
 * This is a modified version of the QuickAction widget
 * found here: http://www.londatiga.net/it/how-to-create-quickaction-dialog-in-android/
 *
 *
 * Modified by: Rajesh
 *
 */

package rajesh.quickactiondemo.widget;
import rajesh.quickactiondemo.R;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class QuickActionView extends CustomPopupWindow implements OnClickListener, DialogInterface {

	private final View root;
	private final ImageView mArrowUp;
	private final ImageView mArrowDown;
	private final LayoutInflater inflater;
	private final Context context;
	private DialogInterface.OnClickListener mClickListener;

	protected ViewGroup mTrack;
	protected ScrollView scroller;
	protected BaseAdapter mAdapter;
	protected int mNumColumns = -1;

	public QuickActionView( View anchor ) {
		super( anchor );

		context = anchor.getContext();
		inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

		root = (ViewGroup) inflater.inflate( R.layout.action_popup_vertical, null );
		mArrowDown = (ImageView) root.findViewById( R.id.arrow_down );
		mArrowUp = (ImageView) root.findViewById( R.id.arrow_up );

		setContentView( root );
		mTrack = (ViewGroup) root.findViewById( R.id.tracks );
		scroller = (ScrollView) root.findViewById( R.id.scroller );
	}

	/**
	 * Set the number of columns per row
	 * -1 for auto columns
	 * @param value
	 */
	public void setNumColumns( int value ) {
		mNumColumns = value;
	}

	public int getNumColumns() {
		return mNumColumns;
	}

	public void setAdapter( BaseAdapter adapter ) {
		mAdapter = adapter;
	}

	public static QuickActionView Builder( View anchor ) {
		return new QuickActionView( anchor );
	}

	public QuickActionView setOnClickListener( DialogInterface.OnClickListener listener ) {
		mClickListener = listener;
		return this;
	}

	public void show() {
		preShow();

		int xPos, yPos;

		int[] location = new int[2];
		anchor.getLocationOnScreen( location );
		Rect anchorRect = new Rect( location[0], location[1], location[0] + anchor.getWidth(), location[1] + anchor.getHeight() );
		createActionList();
		root.setLayoutParams( new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT ) );
		root.measure( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );

		int rootHeight = root.getMeasuredHeight();
		int rootWidth = root.getMeasuredWidth();

		int screenWidth = windowManager.getDefaultDisplay().getWidth();
		int screenHeight = windowManager.getDefaultDisplay().getHeight();

		// check x position
		final int anchorCenterX = anchorRect.centerX();

		xPos = anchorRect.left;

		if( anchorCenterX - (rootWidth / 2) >= 0 ) {
			xPos = anchorCenterX - (rootWidth / 2);
			if( xPos + rootWidth > screenWidth ) {
				xPos -= (xPos + rootWidth) - screenWidth;
			}
		} else {
			xPos = 0;
		}

		int dyTop = anchorRect.top;
		int dyBottom = screenHeight - anchorRect.bottom;

		boolean onTop = (dyTop > dyBottom) ? true : false;

		if( onTop ) {
			if( rootHeight > dyTop ) {
				yPos = 15;
				LayoutParams l = scroller.getLayoutParams();
				l.height = dyTop - anchor.getHeight();
			} else {
				yPos = anchorRect.top - rootHeight;
			}
		} else {
			yPos = anchorRect.bottom;

			if( rootHeight > dyBottom ) {
				LayoutParams l = scroller.getLayoutParams();
				l.height = dyBottom;
			}
		}

		showArrow( ((onTop) ? R.id.arrow_down : R.id.arrow_up), anchorRect.centerX() - xPos );
		setAnimationStyle( screenWidth, anchorRect.centerX(), onTop );
		window.showAtLocation( anchor, Gravity.NO_GRAVITY, xPos, yPos );
		root.getLocationOnScreen( location );
	}

	@SuppressWarnings("unused")
	private void setAnimationStyle( int screenWidth, int requestedX, boolean onTop ) {
		int arrowPos = requestedX - mArrowUp.getMeasuredWidth() / 2;
		window.setAnimationStyle( R.style.Animations_PopDownMenu_Fade );
	}

	protected void createActionList() {

		final int screenWidth = windowManager.getDefaultDisplay().getWidth();
		final int numItems = mAdapter.getCount();

		View view;
		ViewGroup parent = null;
		boolean needNewRow = true;

		for( int i = 0; i < numItems; i++ ) {

			if( i % mNumColumns == 0 && mNumColumns > 0 ) {
				needNewRow = true;
			}

			//======================== OUR_DESIGNBVIEW_WRAP_TO_FILL=================================
			if( needNewRow ) {
				parent = new LinearLayout( context );
				android.widget.LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT,
						LayoutParams.FILL_PARENT );
				params.weight = 1;
				parent.setLayoutParams( params );
				mTrack.addView( parent );
			}

			view = mAdapter.getView( i, null, parent );
			view.setFocusable( true );
			view.setClickable( true );
			view.setOnClickListener( this );
			addActionView( view, parent );

			parent.measure( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
			int padding = mTrack.getPaddingLeft() + mTrack.getPaddingRight();
			int parentWidth = parent.getMeasuredWidth() + padding;
			int childWidth = parentWidth / parent.getChildCount();
			
			needNewRow = false;
			if( parentWidth + childWidth > screenWidth ) {
				mNumColumns = -1;
				needNewRow = true;
			}
		}
	}

	protected void addActionView( View view, ViewGroup parent ) {
		parent.addView( view );
	}

	private void showArrow( int whichArrow, int requestedX ) {
		final View showArrow = (whichArrow == R.id.arrow_up) ? mArrowUp : mArrowDown;
		final View hideArrow = (whichArrow == R.id.arrow_up) ? mArrowDown : mArrowUp;

		final int arrowWidth = mArrowUp.getMeasuredWidth();

		showArrow.setVisibility( View.VISIBLE );
		ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) showArrow.getLayoutParams();
		param.leftMargin = requestedX - arrowWidth / 2;
		hideArrow.setVisibility( View.INVISIBLE );
	}

	@Override
	public void onClick( View v ) {
		int index = getChildIndex( v );

		if( index > -1 ) {
			if( mClickListener != null ) {
				mClickListener.onClick( this, index );
			}
		}
	}

	private int getChildIndex( View v ) {

		ViewGroup parent = (ViewGroup) v.getParent();
		int index = getChildIndex( v, parent );
		int index2 = getRowIndex( parent, mTrack );

		return index + index2;
	}

	private int getChildIndex( View v, ViewGroup parent ) {

		for( int i = 0; i < parent.getChildCount(); i++ ) {
			if( parent.getChildAt( i ).equals( v ) )
				return i;
		}
		return -1;
	}

	private int getRowIndex( ViewGroup v, ViewGroup parent ) {

		int index = 0;
		for( int i = 0; i < parent.getChildCount(); i++ ) {
			if( parent.getChildAt( i ).equals( v ) )
				return index;
			index += v.getChildCount();
		}
		return 0;
	}

	@Override
	public void cancel() {
		this.dismiss();
	}
}