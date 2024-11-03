package pt.psoft.g1.psoftg1.idgeneratormanagement;

public class AlphanumericIdGenerator implements IdGeneratorType {
    @Override
    public String generateId() {
        return IdGenerator.generateAlphanumericId();
    }
}