package se.anosh.spctag.emulator.factory;

import java.util.Objects;

public abstract class Emulator {
    
    protected Emulator(Name name, Integer code) {
        this.name = Objects.requireNonNullElse(name, Name.Unknown);

        // the model object knows whether its tag format is binary or text,
        // so it knows where to put this value. But this object (Emulator)
        // does not need to know what it is
        this.code = Objects.requireNonNull(code); //auto-unboxing
    }
    
    private final Name name;
    final int code;

    public Name getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Emulator{" + "name=" + name + ", offset= 0x" + Integer.toHexString(code) + '}';
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + code;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Emulator other = (Emulator) obj;
		if (code != other.code)
			return false;
		if (name != other.name)
			return false;
		return true;
	}

}
