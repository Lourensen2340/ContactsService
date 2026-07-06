package com.yaskondrichin.ContactsService.domain.generator;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.UUID;

public class UuidV7Generator implements IdentifierGenerator {
    private static final SecureRandom random = new SecureRandom();

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        byte[] value = new byte[16];
        random.nextBytes(value);

        long timestamp = System.currentTimeMillis();

        // 48 бит для таймштампа (время создания)
        value[0] = (byte) (timestamp >> 40);
        value[1] = (byte) (timestamp >> 32);
        value[2] = (byte) (timestamp >> 24);
        value[3] = (byte) (timestamp >> 16);
        value[4] = (byte) (timestamp >> 8);
        value[5] = (byte) timestamp;

        // Выставляем версию 7 (0111) в 6-м байте
        value[6] = (byte) ((value[6] & 0x0F) | 0x70);
        // Выставляем вариант RFC 4122/9562 (10xx) в 8-м байте
        value[8] = (byte) ((value[8] & 0x3F) | 0x80);

        ByteBuffer buffer = ByteBuffer.wrap(value);
        return new UUID(buffer.getLong(), buffer.getLong());
    }
}
