package org.matsim.episim.policy;

import org.junit.Test;
import org.matsim.episim.model.FaceMask;

import static org.assertj.core.api.Assertions.assertThat;

public class RestrictionTest {

	/**
	 * Helper function to allow updating restrictions for tests.
	 */
	public static Restriction update(Restriction r, Restriction other) {
		r.update(other);
		return r;
	}

	@Test
	public void merge() {

		Restriction r = Restriction.of(0.8);

		r.merge(Restriction.ofMask(FaceMask.CLOTH, 0.5).asMap());
		r.merge(Restriction.ofCiCorrection(0.5).asMap());
		r.merge(Restriction.ofGroupSize(20).asMap());

		assertThat(r.getRemainingFraction()).isEqualTo(0.8);
		assertThat(r.getCiCorrection()).isEqualTo(0.5);

		assertThat(r.getMaskUsage().get(FaceMask.NONE)).isEqualTo(0.5);
		assertThat(r.getMaskUsage().get(FaceMask.CLOTH)).isEqualTo(1);

		assertThat(r.getMaxGroupSize()).isEqualTo(20);

		//assertThatExceptionOfType(IllegalArgumentException.class)
		//		.isThrownBy(() -> r.merge(Restriction.ofExposure(0.4).asMap()));

	}

	@Test
	public void closingHours() {

		Restriction r = Restriction.ofClosingHours(5, 9);

		assertThat(r.getClosingHours()).contains(new Restriction.ClosingHours(5 * 3600, 9 * 3600));


		// not overwritten
		r.merge(Restriction.ofClosingHours(17, 20).asMap());
		assertThat(r.getClosingHours()).contains(new Restriction.ClosingHours(5 * 3600, 9 * 3600));
	}

}
