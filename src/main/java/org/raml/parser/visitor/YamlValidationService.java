package org.raml.parser.visitor;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.rule.ValidationResult;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;

public class YamlValidationService
{

    private List<ValidationResult> errorMessage;
    private YamlValidator yamlValidator;
    private ResourceLoader resourceLoader;
    private TagResolver[] tagResolvers;

    protected YamlValidationService(ResourceLoader resourceLoader, YamlValidator yamlValidator, TagResolver[] tagResolvers)
    {
        this.resourceLoader = resourceLoader;
        this.yamlValidator = yamlValidator;
        this.errorMessage = new ArrayList<ValidationResult>();
        this.tagResolvers = tagResolvers;
    }

    public List<ValidationResult> validate(String content)
    {
        Yaml yamlParser = new Yaml();

        try
        {
            NodeVisitor nodeVisitor = new NodeVisitor(yamlValidator, resourceLoader, tagResolvers);
            Node root = yamlParser.compose(new StringReader(content));
            errorMessage.addAll(preValidation((MappingNode) root));
            if (errorMessage.isEmpty() && root.getNodeId() == NodeId.mapping)
            {
                nodeVisitor.visitDocument((MappingNode) root);
            }
        }
        catch (MarkedYAMLException mye)
        {
            errorMessage.add(ValidationResult.createErrorResult(mye.getProblem(), mye.getProblemMark(), null));
        }
        catch (YAMLException ex)
        {
            errorMessage.add(ValidationResult.createErrorResult(ex.getMessage()));
        }

        errorMessage.addAll(yamlValidator.getMessages());
        return errorMessage;
    }

    protected List<ValidationResult> preValidation(MappingNode root)
    {
        //template method
        return new ArrayList<ValidationResult>();
    }

}