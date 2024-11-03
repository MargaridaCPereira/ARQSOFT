package pt.psoft.g1.psoftg1.idgeneratormanagement;

public class HexadecimalIdGenerator implements IdGeneratorType {
    @Override
    public String generateId() {
        return IdGenerator.generateHexadecimalId();
    }
}