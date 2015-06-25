package cn.o.app.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanCache {

	// 属性节点哈希缓存
	public static class PropertyHash {

		protected long mHash;

		protected Object mPorperty;

		public long getHash() {
			return mHash;
		}

		public void setHash(long value) {
			mHash = value;
		}

		public Object getProperty() {
			return mPorperty;
		}

		public void setProperty(Object value) {
			mPorperty = value;
		}

		// 判断哈希是否有效 属性被改写则导致失效
		public boolean isValid() {
			return mHash == HashCode.hashCode(mPorperty);
		}
	}

	protected Map<String, PropertyHash> mCache;
	protected Object mTarget;

	public BeanCache() {

	}

	public BeanCache(Object target) {
		mTarget = target;
	}

	public Object getTarget() {
		return mTarget;
	}

	public void setTarget(Object target) {
		mTarget = target;
		if (mCache != null) {
			mCache.clear();
		}
	}

	// 将缓存写入对象，返回改变的属性
	public List<String> fromTarget() {
		if (mTarget == null) {
			return null;
		}
		if (mCache == null) {
			mCache = new HashMap<String, PropertyHash>();
		}
		ArrayList<String> changed = new ArrayList<String>();
		for (OField f : OField.getFields(mTarget.getClass())) {
			try {
				String name = f.getName();
				Object fValue = f.get(mTarget);
				long fHashCode = HashCode.hashCode(fValue);
				boolean isChanged = false;
				PropertyHash propertyHash = mCache.get(name);
				if (propertyHash == null) {
					isChanged = true;
					propertyHash = new PropertyHash();
					mCache.put(name, propertyHash);
				} else {
					if (!propertyHash.isValid()) {
						isChanged = true;
					} else {
						if (propertyHash.mHash != fHashCode) {
							isChanged = true;
						}
					}
				}
				if (isChanged) {
					propertyHash.mHash = fHashCode;
					propertyHash.mPorperty = fValue;
					changed.add(name);
				}
			} catch (Exception e) {

			}
		}
		return changed.size() != 0 ? changed : null;
	}

	// 将对象写入缓存，返回改变的属性
	public List<String> toTarget() {
		if (mTarget == null) {
			return null;
		}
		if (mCache == null) {
			mCache = new HashMap<String, PropertyHash>();
		}
		ArrayList<String> changed = new ArrayList<String>();
		for (OField f : OField.getFields(mTarget.getClass())) {
			try {
				String name = f.getName();
				Object fValue = f.get(mTarget);
				long fHashCode = HashCode.hashCode(fValue);
				boolean isChanged = false;
				PropertyHash propertyHash = mCache.get(name);
				if (propertyHash == null) {
					isChanged = true;
				} else {
					if (!propertyHash.isValid()) {
						isChanged = true;
						propertyHash.mPorperty = null;
					} else {
						if (propertyHash.mHash != fHashCode) {
							isChanged = true;
							f.set(mTarget, propertyHash.mPorperty);
						}
					}
				}
				if (isChanged) {
					changed.add(name);
				}
			} catch (Exception e) {

			}
		}
		return changed.size() != 0 ? changed : null;
	}

	public void clear() {
		clear(null);
	}

	public void clear(List<String> properties) {
		if (mCache == null) {
			return;
		}
		if (properties == null) {
			mCache.clear();
		} else {
			for (String s : properties) {
				mCache.remove(s);
			}
		}
	}

}
