package com.intuitivus.pdi.steps.spreadsheet;

import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

public class IntuitivusSpreadsheetStepMeta extends BaseStepMeta implements StepMetaInterface
{

	private static Class<?> PKG = IntuitivusSpreadsheetStepMeta.class;
	private String version = null;

	private String driveUser;
	private String drivePassword;
	private String driveDocumentId;
	private String driveSheet;
	private String driveRange;
	private HeaderType driveHeader;
	private boolean driveAcceptEmptyLines;

	private boolean adoptOutput = true; // for full previews

	private String outputField[];
	private String outputDefault[];
	private int outputType[];
	private String outputFormat[];
	private String outputCurrency[];
	private String outputDecimal[];
	private String outputGroup[];
	private int outputLength[];
	private int outputPrecision[];

	public enum HeaderType
	{
		NONE, ROW1, FIRST;
	}

	public IntuitivusSpreadsheetStepMeta()
	{
		super();
	}

	public StepDialogInterface getDialog(Shell shell, StepMetaInterface meta, TransMeta transMeta, String name)
	{
		return new IntuitivusSpreadsheetStepDialog(shell, meta, transMeta, name);
	}

	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr, TransMeta transMeta, Trans disp)
	{
		return new IntuitivusSpreadsheetStep(stepMeta, stepDataInterface, cnr, transMeta, disp);
	}

	public StepDataInterface getStepData()
	{
		return new IntuitivusSpreadsheetStepData();
	}

	public boolean adoptOutput()
	{
		return adoptOutput;
	}

	public void setAdoptOutput(boolean adoptOutput)
	{
		this.adoptOutput = adoptOutput;
	}

	public String getDriveUser()
	{
		return driveUser;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public void setDriveUser(String driveUser)
	{
		this.driveUser = driveUser;
	}

	public String getDrivePassword()
	{
		return drivePassword;
	}

	public void setDrivePassword(String drivePassword)
	{
		this.drivePassword = drivePassword;
	}

	public String getDriveDocumentId()
	{
		return driveDocumentId;
	}

	public void setDriveDocumentId(String driveDocumentId)
	{
		this.driveDocumentId = driveDocumentId;
	}

	public String getDriveSheet()
	{
		return driveSheet;
	}

	public void setDriveSheet(String driveSheet)
	{
		this.driveSheet = driveSheet;
	}

	public String getDriveRange()
	{
		return driveRange;
	}

	public void setDriveRange(String driveRange)
	{
		this.driveRange = driveRange;
	}

	public HeaderType getDriveHeader()
	{
		return driveHeader;
	}

	public void setDriveHeader(int driveHeader)
	{
		this.driveHeader = HeaderType.values()[driveHeader];
	}

	public void setDriveHeader(HeaderType driveHeader)
	{
		this.driveHeader = driveHeader;
	}

	public boolean isDriveAcceptEmptyLines()
	{
		return driveAcceptEmptyLines;
	}

	public void setDriveAcceptEmpty(boolean driveAcceptEmpty)
	{
		this.driveAcceptEmptyLines = driveAcceptEmpty;
	}

	public String[] getOutputField()
	{
		return outputField;
	}

	public void setOutputField(String[] outputField)
	{
		this.outputField = outputField;
	}

	public String[] getOutputDefault()
	{
		return outputDefault;
	}

	public void setOutputDefault(String[] outputDefault)
	{
		this.outputDefault = outputDefault;
	}

	public int[] getOutputType()
	{
		return outputType;
	}

	public void setOutputType(int[] outputType)
	{
		this.outputType = outputType;
	}

	public String[] getOutputFormat()
	{
		return outputFormat;
	}

	public void setOutputFormat(String[] outputFormat)
	{
		this.outputFormat = outputFormat;
	}

	public String[] getOutputCurrency()
	{
		return outputCurrency;
	}

	public void setOutputCurrency(String[] outputCurrency)
	{
		this.outputCurrency = outputCurrency;
	}

	public String[] getOutputDecimal()
	{
		return outputDecimal;
	}

	public void setOutputDecimal(String[] outputDecimal)
	{
		this.outputDecimal = outputDecimal;
	}

	public String[] getOutputGroup()
	{
		return outputGroup;
	}

	public void setOutputGroup(String[] outputGroup)
	{
		this.outputGroup = outputGroup;
	}

	public int[] getOutputLength()
	{
		return outputLength;
	}

	public void setOutputLength(int[] outputLength)
	{
		this.outputLength = outputLength;
	}

	public int[] getOutputPrecision()
	{
		return outputPrecision;
	}

	public void setOutputPrecision(int[] outputPrecision)
	{
		this.outputPrecision = outputPrecision;
	}

	public Object clone()
	{
		IntuitivusSpreadsheetStepMeta metaClone = (IntuitivusSpreadsheetStepMeta) super.clone();
		int total = outputField.length;

		metaClone.allocate(total);

		for (int i = 0; i < total; i++)
		{
			metaClone.outputField[i] = outputField[i];
			metaClone.outputDefault[i] = outputDefault[i];
			metaClone.outputType[i] = outputType[i];
			metaClone.outputCurrency[i] = outputCurrency[i];
			metaClone.outputDecimal[i] = outputDecimal[i];
			metaClone.outputFormat[i] = outputFormat[i];
			metaClone.outputGroup[i] = outputGroup[i];
			metaClone.outputLength[i] = outputLength[i];
			metaClone.outputPrecision[i] = outputPrecision[i];
		}

		return metaClone;
	}

	public void allocate(int total)
	{
		outputField = new String[total];
		outputDefault = new String[total];
		outputType = new int[total];
		outputFormat = new String[total];
		outputDecimal = new String[total];
		outputGroup = new String[total];
		outputLength = new int[total];
		outputPrecision = new int[total];
		outputCurrency = new String[total];
	}

	public String getXML() throws KettleValueException
	{
		StringBuffer retval = new StringBuffer(150);

		retval.append("    ").append(XMLHandler.addTagValue("version", version));
		retval.append("    ").append(XMLHandler.addTagValue("user", driveUser));
		retval.append("    ").append(XMLHandler.addTagValue("password", drivePassword));
		retval.append("    ").append(XMLHandler.addTagValue("documentId", driveDocumentId));
		retval.append("    ").append(XMLHandler.addTagValue("range", driveRange));
		retval.append("    ").append(XMLHandler.addTagValue("sheet", driveSheet));
		retval.append("    ").append(XMLHandler.addTagValue("header", driveHeader.toString()));
		retval.append("    ").append(XMLHandler.addTagValue("emptyLines", Boolean.toString(driveAcceptEmptyLines)));

		for (int i = 0; i < outputField.length; i++)
		{
			retval.append("      <lookup>");
			retval.append("        ").append(XMLHandler.addTagValue("outfield", outputField[i]));
			retval.append("        ").append(XMLHandler.addTagValue("default", outputDefault[i]));
			retval.append("        ").append(XMLHandler.addTagValue("type", ValueMeta.getTypeDesc(outputType[i])));
			retval.append("        ").append(XMLHandler.addTagValue("format", outputFormat[i]));
			retval.append("        ").append(XMLHandler.addTagValue("decimal", outputDecimal[i]));
			retval.append("        ").append(XMLHandler.addTagValue("group", outputGroup[i]));
			retval.append("        ").append(XMLHandler.addTagValue("length", outputLength[i]));
			retval.append("        ").append(XMLHandler.addTagValue("precision", outputPrecision[i]));
			retval.append("        ").append(XMLHandler.addTagValue("currency", outputCurrency[i]));

			retval.append("      </lookup>");
		}

		return retval.toString();
	}

	public void loadXML(Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore) throws KettleXMLException
	{

		try
		{

			version = XMLHandler.getTagValue(stepnode, "version");
			driveUser = XMLHandler.getTagValue(stepnode, "user");
			drivePassword = XMLHandler.getTagValue(stepnode, "password");
			driveDocumentId = XMLHandler.getTagValue(stepnode, "documentId");
			driveRange = XMLHandler.getTagValue(stepnode, "range");
			driveSheet = XMLHandler.getTagValue(stepnode, "sheet");
			driveHeader = HeaderType.valueOf(XMLHandler.getTagValue(stepnode, "header"));
			driveAcceptEmptyLines = Boolean.parseBoolean(XMLHandler.getTagValue(stepnode, "emptyLines"));

			int total = XMLHandler.countNodes(stepnode, "lookup");
			allocate(total);

			for (int i = 0; i < total; i++)
			{
				Node knode = XMLHandler.getSubNodeByNr(stepnode, "lookup", i);

				outputField[i] = XMLHandler.getTagValue(knode, "outfield");
				outputDefault[i] = XMLHandler.getTagValue(knode, "default");
				outputType[i] = ValueMeta.getType(XMLHandler.getTagValue(knode, "type"));
				outputFormat[i] = XMLHandler.getTagValue(knode, "format");
				outputDecimal[i] = XMLHandler.getTagValue(knode, "decimal");
				outputGroup[i] = XMLHandler.getTagValue(knode, "group");
				outputLength[i] = Const.toInt(XMLHandler.getTagValue(knode, "length"), -1);
				outputPrecision[i] = Const.toInt(XMLHandler.getTagValue(knode, "precision"), -1);
				outputCurrency[i] = XMLHandler.getTagValue(knode, "currency");

				if (outputType[i] < 0)
				{
					outputType[i] = ValueMetaInterface.TYPE_STRING;
				}

			}

		} catch (Exception e)
		{
			throw new KettleXMLException(BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.error.reporead"), e);
		}

	}

	public void saveRep(Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step) throws KettleException
	{
		try
		{
			rep.saveStepAttribute(id_transformation, id_step, "version", version);
			rep.saveStepAttribute(id_transformation, id_step, "user", driveUser);
			rep.saveStepAttribute(id_transformation, id_step, "password", drivePassword);
			rep.saveStepAttribute(id_transformation, id_step, "documentId", driveDocumentId);
			rep.saveStepAttribute(id_transformation, id_step, "range", driveRange);
			rep.saveStepAttribute(id_transformation, id_step, "sheet", driveSheet);
			rep.saveStepAttribute(id_transformation, id_step, "header", driveHeader.toString());
			rep.saveStepAttribute(id_transformation, id_step, "emptyLines", Boolean.toString(driveAcceptEmptyLines));

			for (int i = 0; i < outputField.length; i++)
			{
				rep.saveStepAttribute(id_transformation, id_step, i, "lookup_outfield", outputField[i]);
				rep.saveStepAttribute(id_transformation, id_step, i, "lookup_default", outputDefault[i]);
				rep.saveStepAttribute(id_transformation, id_step, i, "lookup_type", ValueMeta.getTypeDesc(outputType[i]));
				rep.saveStepAttribute(id_transformation, id_step, i, "lookup_format", outputFormat[i]);
				rep.saveStepAttribute(id_transformation, id_step, i, "lookup_decimal", outputDecimal[i]);
				rep.saveStepAttribute(id_transformation, id_step, i, "lookup_group", outputGroup[i]);
				rep.saveStepAttribute(id_transformation, id_step, i, "lookup_length", outputLength[i]);
				rep.saveStepAttribute(id_transformation, id_step, i, "lookup_precision", outputPrecision[i]);
				rep.saveStepAttribute(id_transformation, id_step, i, "lookup_currency", outputCurrency[i]);

			}

		} catch (Exception e)
		{
			throw new KettleException(BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.error.reposave") + id_step, e);
		}
	}

	public void readRep(Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases) throws KettleException
	{
		try
		{
			version = rep.getStepAttributeString(id_step, "version");
			driveUser = rep.getStepAttributeString(id_step, "user");
			drivePassword = rep.getStepAttributeString(id_step, "password");
			driveDocumentId = rep.getStepAttributeString(id_step, "documentId");
			driveRange = rep.getStepAttributeString(id_step, "range");
			driveSheet = rep.getStepAttributeString(id_step, "sheet");
			driveHeader = HeaderType.valueOf(rep.getStepAttributeString(id_step, "header"));
			driveAcceptEmptyLines = Boolean.parseBoolean(rep.getStepAttributeString(id_step, "emptyLines"));

			int total = rep.countNrStepAttributes(id_step, "lookup_keyfield");
			allocate(total);

			for (int i = 0; i < total; i++)
			{
				outputField[i] = rep.getStepAttributeString(id_step, i, "lookup_outfield");
				outputDefault[i] = rep.getStepAttributeString(id_step, i, "lookup_default");
				outputType[i] = ValueMeta.getType(rep.getStepAttributeString(id_step, i, "lookup_type"));
				outputFormat[i] = rep.getStepAttributeString(id_step, i, "lookup_format");
				outputDecimal[i] = rep.getStepAttributeString(id_step, i, "lookup_decimal");
				outputGroup[i] = rep.getStepAttributeString(id_step, i, "lookup_group");
				outputLength[i] = Const.toInt(rep.getStepAttributeString(id_step, i, "lookup_length"), -1);
				outputPrecision[i] = Const.toInt(rep.getStepAttributeString(id_step, i, "lookup_precision"), -1);
				outputCurrency[i] = rep.getStepAttributeString(id_step, i, "lookup_currency");

			}

		} catch (Exception e)
		{
			throw new KettleException(BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.error.reporead"), e);
		}
	}

	public void getFields(RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space, Repository repository, IMetaStore metaStore)
			throws KettleStepException
	{

		for (int i = 0; i < outputField.length; i++)
		{
			ValueMetaInterface valueMeta = new ValueMeta(outputField[i], outputType[i]);
			valueMeta.setLength(outputLength[i]);
			valueMeta.setPrecision(outputPrecision[i]);
			valueMeta.setCurrencySymbol(outputCurrency[i]);
			valueMeta.setConversionMask(outputFormat[i]);
			valueMeta.setDecimalSymbol(outputDecimal[i]);
			valueMeta.setGroupingSymbol(outputGroup[i]);
			valueMeta.setOrigin(name);
			inputRowMeta.addValueMeta(valueMeta);
		}

	}

	public void getFieldsForPreview(RowMetaInterface inputRowMeta, String name, int fieldsLength) throws KettleStepException
	{
		for (int i = 0; i < fieldsLength; i++)
		{
			ValueMetaInterface valueMeta = new ValueMeta("Field_" + i, ValueMeta.getType("String"));
			inputRowMeta.addValueMeta(valueMeta);
		}
	}

	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev, String input[], String output[], RowMetaInterface info, VariableSpace space,
			Repository repository, IMetaStore metaStore)
	{
		CheckResult cr = new CheckResult(CheckResult.TYPE_RESULT_OK, BaseMessages.getString(PKG, "com.intuitivus.pdi.steps.spreadsheet.ok"), stepMeta);
		remarks.add(cr);
	}

	@Override
	public void setDefault()
	{
		allocate(0);
	}

	public boolean hasDataToConnect()
	{
		return driveUser != null && !driveUser.isEmpty() && drivePassword != null && !drivePassword.isEmpty() && driveDocumentId != null && !driveDocumentId.isEmpty();
	}

}
