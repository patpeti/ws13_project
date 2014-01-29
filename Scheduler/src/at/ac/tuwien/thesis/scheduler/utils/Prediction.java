package at.ac.tuwien.thesis.scheduler.utils;

import at.ac.tuwien.thesis.scheduler.model.cloudModel.Machine;

public class Prediction {

	Machine m;
	String dimension;
	Integer step;
	int age = 0;
	boolean toDelete = false;

	public Prediction(Machine m, String dimension, Integer step) {
		super();
		this.m = m;
		this.dimension = dimension;
		this.step = step;
	}

	public Machine getM() {
		return m;
	}

	public void setM(Machine m) {
		this.m = m;
	}

	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}

	public Integer getStep() {
		return step;
	}

	public void setStep(Integer step) {
		this.step = step;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dimension == null) ? 0 : dimension.hashCode());
		result = prime * result + ((m == null) ? 0 : m.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Prediction other = (Prediction) obj;
		if (dimension == null) {
			if (other.dimension != null)
				return false;
		} else if (!dimension.equals(other.dimension))
			return false;
		if (m == null) {
			if (other.m != null)
				return false;
		} else if (!m.equals(other.m))
			return false;
		return true;
	}

	public void increaseAge() {
		this.age++;
	}

	public int getAge() {
		return this.age;
	}

	public boolean isToDelete() {
		return toDelete;
	}

	public void setToDelete(boolean toDelete) {
		this.toDelete = toDelete;
	}

}
