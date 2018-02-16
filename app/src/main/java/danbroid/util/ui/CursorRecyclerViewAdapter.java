/*
 * Copyright 2016 Dan Brough
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package danbroid.util.ui;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;

public abstract class CursorRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends
		RecyclerView.Adapter<VH> {
	private final DataSetObserver dataSetObserver;
	private Cursor cursor;
	private boolean dataValid;
	private int rowIdColumn;

	public CursorRecyclerViewAdapter() {
		this(null);
	}

	public CursorRecyclerViewAdapter(Cursor cursor) {
		setHasStableIds(true);
		dataValid = cursor != null;
		rowIdColumn = dataValid ? cursor.getColumnIndex("_id") : -1;
		dataSetObserver = new NotifyingDataSetObserver();
		if (cursor != null) {
			cursor.registerDataSetObserver(dataSetObserver);
		}
	}

	public Cursor getCursor() {
		return cursor;
	}

	@Override
	public int getItemCount() {
		if (dataValid && cursor != null) {
			return cursor.getCount();
		}
		return 0;
	}

	@Override
	public long getItemId(int position) {
		if (dataValid && cursor != null && cursor.moveToPosition(position)) {
			return cursor.getLong(rowIdColumn);
		}
		return 0;
	}

	public abstract void onBindViewHolder(VH viewHolder, Cursor cursor);

	@Override
	public final void onBindViewHolder(VH viewHolder, int position) {
		if (!dataValid) {
			throw new IllegalStateException("this should only be called when the cursor is valid");
		}
		if (!cursor.moveToPosition(position)) {
			throw new IllegalStateException("couldn't move cursor to position " + position);
		}
		onBindViewHolder(viewHolder, cursor);
	}


	public Cursor swapCursor(Cursor newCursor) {

		if (newCursor == cursor) {
			return null;
		}
		final Cursor oldCursor = cursor;
		if (oldCursor != null && dataSetObserver != null) {
			oldCursor.unregisterDataSetObserver(dataSetObserver);
		}
		cursor = newCursor;
		if (cursor != null) {
			if (dataSetObserver != null) {
				cursor.registerDataSetObserver(dataSetObserver);
			}
			rowIdColumn = newCursor.getColumnIndexOrThrow("_id");
			dataValid = true;
			notifyDataSetChanged();
		} else {
			rowIdColumn = -1;
			dataValid = false;
			notifyDataSetChanged();
		}
		return oldCursor;
	}

	private class NotifyingDataSetObserver extends DataSetObserver {
		@Override
		public void onChanged() {
			super.onChanged();
			dataValid = true;
			notifyDataSetChanged();
		}

		@Override
		public void onInvalidated() {
			super.onInvalidated();
			dataValid = false;
			notifyDataSetChanged();
		}
	}

}