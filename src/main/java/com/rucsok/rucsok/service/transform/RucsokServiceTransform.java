package com.rucsok.rucsok.service.transform;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rucsok.rucsok.domain.Rucsok;
import com.rucsok.rucsok.domain.SingleRucsok;
import com.rucsok.rucsok.repository.domain.RucsokEntity;
import com.rucsok.user.repository.domain.UserEntity;
import com.rucsok.user.transform.UserTransformer;

@Component
public class RucsokServiceTransform {

	@Autowired
	private RucsokTypeTransform rucsokTypeTransform;

	@Autowired
	private UserTransformer userTransformer;

	public List<Rucsok> transformToRucsok(List<RucsokEntity> rucsoks) {
		return rucsoks.stream().map(r -> transformToRucsok(r)).collect(Collectors.toList());
	}

	public RucsokEntity transformToRucsokEntity(Rucsok rucsok) {
		RucsokEntity result = new RucsokEntity();
		result.setImageUrl(rucsok.getImageUrl());
		result.setLink(rucsok.getLink());
		result.setTitle(rucsok.getTitle());
		result.setVideoUrl(rucsok.getVideoUrl());
		return result;
	}

	public Rucsok transformToRucsok(RucsokEntity rucsokEntity) {
		Rucsok result = new Rucsok();
		result.setId(rucsokEntity.getId());
		result.setTitle(rucsokEntity.getTitle());
		result.setImageUrl(rucsokEntity.getImageUrl());
		result.setLink(rucsokEntity.getLink());
		result.setVideoUrl(rucsokEntity.getVideoUrl());
		result.setType(rucsokTypeTransform.getRucsokTypeFromEntity(rucsokEntity));
		UserEntity user = rucsokEntity.getUser();
		if(user==null){
			System.out.println("dafug");
		}
		result.setUser(userTransformer.transformEntityToUser(user));
		return result;
	}

	public SingleRucsok transformToSingleRucsok(List<RucsokEntity> rucsoks) {
		SingleRucsok result = new SingleRucsok();
		if (containsOnlyOneRucsok(rucsoks)) {
			setFirstElementToCurrentRucsok(rucsoks, result);
		} else {
			setFirstToPreviousRucsok(rucsoks, result);
			setSecondToCurrentRucsok(rucsoks, result);
			setThirdToNextRucsok(rucsoks, result);
		}
		return result;
	}

	private void setFirstElementToCurrentRucsok(List<RucsokEntity> rucsoks, SingleRucsok result) {
		result.setCurrent(transformToRucsok(rucsoks.get(0)));
	}

	private boolean containsOnlyOneRucsok(List<RucsokEntity> rucsoks) {
		return 1 == rucsoks.size();
	}

	private void setSecondToCurrentRucsok(List<RucsokEntity> rucsoks, SingleRucsok result) {
		if (rucsoks.size() > 0) {
			result.setCurrent(transformToRucsok(rucsoks.get(1)));
		}
	}

	private void setThirdToNextRucsok(List<RucsokEntity> rucsoks, SingleRucsok result) {
		if (rucsoks.size() > 2) {
			result.setNext(transformToRucsok(rucsoks.get(2)));
		}
	}

	private void setFirstToPreviousRucsok(List<RucsokEntity> rucsoks, SingleRucsok result) {
		if (rucsoks.size() > 1) {
			result.setPrevious(transformToRucsok(rucsoks.get(0)));
		}
	}
}
