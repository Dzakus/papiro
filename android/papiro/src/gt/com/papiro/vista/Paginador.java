package gt.com.papiro.vista;

import gt.com.papiro.modelo.Presentable;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.HorizontalScrollView;

public class Paginador extends HorizontalScrollView {
	private static final String TAG = "Paginador";

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;

	private Runnable scrollerTask;
	private int initialPosition;
	private int newCheck = 100;
	private OnScrollStoppedListener onScrollStoppedListener;
	private OnScrollChangedListener onScrollChangedListener;
	private OnPresentarHijoListener onPresentarHijoListener;
	private OnOcultarHijoListener onOcultarHijoListener;
	private int pageWidth;
	private int currentPage;

	private void init() {
		this.pageWidth = ViewGroup.LayoutParams.MATCH_PARENT;
		this.setSmoothScrollingEnabled(true);
		scrollTaskInit();

		gestureDetector = new GestureDetector(this.getContext(), new FlingGestureDetector());
		/*
		 * gestureListener = new View.OnTouchListener() { public boolean
		 * onTouch(View v, MotionEvent event) { return
		 * gestureDetector.onTouchEvent(event); } };
		 * 
		 * this.setOnTouchListener(gestureListener);
		 */
	}

	public Paginador(Context context) {
		super(context);

		init();
	}

	public Paginador(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	public Paginador(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init();
	}

	public void setPageWidth(int pageWidth) {
		this.pageWidth = pageWidth;
	}

	public int getPageWidth() {
		return this.pageWidth;
	}
	
	public View getViewAtCenter() {
		ViewGroup vg = (ViewGroup) this.getChildAt(0);		
		return vg.getChildAt(getCurrentPage());
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		boolean result = super.onTouchEvent(ev);

		if (!gestureDetector.onTouchEvent(ev)) {
			if ((ev.getAction() & MotionEvent.ACTION_UP) > 0) {

				if (pageWidth != 0) {
					if (pageWidth == ViewGroup.LayoutParams.MATCH_PARENT) {
						goToPage(Math.round(((float) this.getScrollX() / (float) this.getWidth())));
					} else {
						goToPage(Math.round(((float) this.getScrollX() / (float) pageWidth)));
					}
				}
			}
		}
		return result;
	}

	public int getCurrentPage() {
		return this.currentPage;
	}

	public int getPageCount() {
		if (this.getChildCount() > 0 && this.getChildAt(0) instanceof ViewGroup) {
			return ((ViewGroup) this.getChildAt(0)).getChildCount();
		}

		return 0;
	}

	public int getHorizontalScrollRange() {
		return this.computeHorizontalScrollRange();
	}

	public void goToPage(int pageNumber) {
		if (pageNumber < 0) {
			pageNumber = 0;
		}

		if (pageNumber >= getPageCount()) {
			pageNumber = getPageCount() - 1;
		}

		smoothScrollToPage(pageNumber);

		if (currentPage == pageNumber) {
			return;
		}

		ocultarHijo(currentPage);

		currentPage = pageNumber;

		// Muestra la informacion de la imagen:

		presentarHijo(pageNumber);
	}

	public void presentarHijo(int numero) {
		ViewGroup vg = (ViewGroup) this.getChildAt(0);
		View child;

		if (vg == null) {
			return;
		}

		child = vg.getChildAt(numero);

		if (child != null && child instanceof Presentable) {
			if (onPresentarHijoListener != null) {
				onPresentarHijoListener.onPresentarHijo((Presentable) child);
			}
			((Presentable) child).presentar();
		}
	}

	public void ocultarHijo(int numero) {
		ViewGroup vg = (ViewGroup) this.getChildAt(0);
		View child;

		if (vg == null) {
			return;
		}

		child = vg.getChildAt(numero);

		if (child != null && child instanceof Presentable) {
			if (onOcultarHijoListener != null) {
				onOcultarHijoListener.onOcultarHijo((Presentable) child);
			}
			((Presentable) child).ocultar();
		}
	}

	public void smoothScrollToPage(int pageNumber) {
		if (pageWidth == ViewGroup.LayoutParams.MATCH_PARENT) {
			this.smoothScrollTo(pageNumber * this.getWidth(), this.getScrollY());
		} else {
			this.smoothScrollTo(pageNumber * pageWidth, this.getScrollY());
		}
	}

	public void smoothScrollToPercent(float percent) {
		if (pageWidth == 0 || pageWidth == ViewGroup.LayoutParams.MATCH_PARENT) {
			this.smoothScrollTo((int) Math.round(Math.floor((percent / 100f) * this.computeHorizontalScrollRange() / this.getWidth()) * this.getWidth()), this.getScrollY());
		} else {
			this.smoothScrollTo((int) Math.round(Math.floor((percent / 100f) * this.computeHorizontalScrollRange() / pageWidth) * pageWidth), this.getScrollY());
		}
	}

	private void scrollTaskInit() {
		scrollerTask = new Runnable() {

			public void run() {
				int newPosition = getScrollX();
				if (initialPosition - newPosition == 0) {// has stopped

					if (onScrollStoppedListener != null) {
						onScrollStopped();
						onScrollStoppedListener.onScrollStopped();
					}
				} else {
					initialPosition = getScrollX();
					Paginador.this.postDelayed(scrollerTask, newCheck);
				}
			}
		};
	}

	public void setOnScrollStoppedListener(
			Paginador.OnScrollStoppedListener listener) {
		onScrollStoppedListener = listener;
	}

	public void setOnPresentarHijoListener(
			OnPresentarHijoListener onPresentarHijoListener) {
		this.onPresentarHijoListener = onPresentarHijoListener;
	}

	public void setOnOcultarHijoListener(OnOcultarHijoListener onOcultarHijoListener) {
		this.onOcultarHijoListener = onOcultarHijoListener;
	}

	public void startScrollerTask() {
		initialPosition = getScrollX();
		Paginador.this.postDelayed(scrollerTask, newCheck);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);

		if (onScrollChangedListener != null) {
			onScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
		}
	}

	protected void onScrollStopped() {
		Log.i(TAG, "Prueba");
	}

	public void setOnScrollChangedLister(
			OnScrollChangedListener onScrollChangedListener) {
		this.onScrollChangedListener = onScrollChangedListener;
	}

	public interface OnScrollStoppedListener {
		void onScrollStopped();
	}

	public interface OnScrollChangedListener {
		void onScrollChanged(View view, int l, int t, int oldl, int oldt);
	}
	
	public interface OnPresentarHijoListener {
		void onPresentarHijo(Presentable presentable);
	}
	
	public interface OnOcultarHijoListener {
		void onOcultarHijo(Presentable presentable);
	}

	class FlingGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			Log.i(TAG, String.format("Fling: %f,%f", velocityX, velocityY));

			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					// Left swipe
					if (Paginador.this.getCurrentPage() >= Paginador.this.getPageCount() - 1) {
						Paginador.this.goToPage(0);
					} else {
						Paginador.this.goToPage(Paginador.this.getCurrentPage() + 1);
					}
					return true;
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					// Right swipe
					if (Paginador.this.getCurrentPage() <= 0) {
						Paginador.this.goToPage(Paginador.this.getPageCount() - 1);
					} else {
						Paginador.this.goToPage(Paginador.this.getCurrentPage() - 1);
					}

					return true;
				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

	}

}
