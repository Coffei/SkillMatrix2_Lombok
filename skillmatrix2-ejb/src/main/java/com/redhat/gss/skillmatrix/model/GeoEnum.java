package com.redhat.gss.skillmatrix.model;

public enum GeoEnum {
	EMEA, NASA, APAC, Pune;

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		if(this == NASA) {
			return "NA/SA";
		} else {
			return super.toString();
		}
	}
	
	public static GeoEnum parseEnum(String value) {
		if("NA/SA".equals(value)) {
			return GeoEnum.NASA;
		} else {
			return GeoEnum.valueOf(value);
		}
	}
	
}
