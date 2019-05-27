package org.openmrs.module.initializer.api.attributes.types;

import org.openmrs.attribute.BaseAttributeType;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AttributeTypeCsvParser extends CsvParser<BaseAttributeType, BaseLineProcessor<BaseAttributeType>> {
	
	private AttributeTypeServiceCompatibility service;
	
	private BaseAttributeTypeLineProcessor lineProcessor;
	
	@Autowired
	public AttributeTypeCsvParser(AttributeTypeServiceCompatibility service, BaseAttributeTypeLineProcessor lineProcessor) {
		this.service = service;
		this.lineProcessor = lineProcessor;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected BaseAttributeType save(BaseAttributeType instance) {
		return service.saveAttributeType(instance);
	}
	
	@Override
	protected void setLineProcessors(String version, String[] headerLine) {
		lineProcessors.clear();
		lineProcessors.add(lineProcessor.setHeaderLine(headerLine));
	}
	
	@Override
	public Domain getDomain() {
		return Domain.ATTRIBUTE_TYPES;
	}
	
}
