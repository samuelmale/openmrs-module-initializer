package org.openmrs.module.initializer.attributes.types;

import java.util.List;
import java.util.Set;

import org.openmrs.ConceptAttributeType;
import org.openmrs.ProgramAttributeType;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.initializer.api.attributes.types.AttributeTypeEnum;
import org.openmrs.module.initializer.api.attributes.types.AttributeTypeServiceImpl1_11;

@SuppressWarnings("rawtypes")
@OpenmrsProfile(openmrsPlatformVersion = "[2.2.0 - 2.3.*]")
public class AttributeTypeServiceImpl2_2 extends AttributeTypeServiceImpl1_11 {
	
	@Override
	public Set<AttributeTypeEnum> getSupportedTypes() {
		Set<AttributeTypeEnum> types = super.getSupportedTypes();
		types.add(AttributeTypeEnum.CONCEPT);
		types.add(AttributeTypeEnum.PROGRAM);
		return types;
	}
	
	@Override
	protected BaseAttributeType save(BaseAttributeType instance) {
		
		instance = super.save(instance);
		
		if (instance instanceof ProgramAttributeType) {
			ProgramAttributeType programAttributeType = (ProgramAttributeType) instance;
			instance = Context.getProgramWorkflowService().saveProgramAttributeType(programAttributeType);
		}
		if (instance instanceof ConceptAttributeType) {
			ConceptAttributeType conceptAttributeType = (ConceptAttributeType) instance;
			instance = Context.getConceptService().saveConceptAttributeType(conceptAttributeType);
		}
		
		return instance;
	}
	
	@Override
	protected BaseAttributeType getByUuid(String uuid, AttributeTypeEnum typeEnum) {
		
		BaseAttributeType attType = super.getByUuid(uuid, typeEnum);
		
		switch (typeEnum) {
			case CONCEPT:
				attType = Context.getConceptService().getConceptAttributeTypeByUuid(uuid);
				break;
			
			case PROGRAM:
				attType = Context.getProgramWorkflowService().getProgramAttributeTypeByUuid(uuid);
				break;
			
			default:
				;
		}
		
		return attType;
	}
	
	@Override
	protected BaseAttributeType getByName(String name, AttributeTypeEnum typeEnum) {
		
		BaseAttributeType attType = super.getByName(name, typeEnum);
		
		switch (typeEnum) {
			case CONCEPT:
				attType = Context.getConceptService().getConceptAttributeTypeByName(name);
				break;
			
			case PROGRAM:
				List<ProgramAttributeType> programAttTypes = Context.getProgramWorkflowService()
				        .getAllProgramAttributeTypes();
				if (programAttTypes != null) {
					for (ProgramAttributeType candidate : programAttTypes) {
						if (name.equals(candidate.getName())) {
							attType = candidate;
							break; // for loop
						}
					}
				}
				break;
			
			default:
				;
		}
		
		return attType;
	}
	
}
