package at.ac.tuwien.thesis.scheduler.enums;

import java.io.Serializable;

public enum ForecastType implements Serializable {

	NONE,NAIVE,NNAR,DECOMP,HW,DSHW,HOURLYDSWH,FOURIER,MAX,MEAN,TBATS,HOURLYTBATS, SPORADIC
}
