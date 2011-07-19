package ca.etsmtl.breaktrough;

public class CompositeKey
{
    private long a_;
    private long b_;

    public CompositeKey(long a, long b)
    {
        a_ = a;
        b_ = b;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (!(obj instanceof CompositeKey))
            return false;

        CompositeKey key = CompositeKey.class.cast(obj);

        return (a_ == key.a_ && b_ == key.b_);
    }

    //@Override
    public int hashCode()
    {
    	return (int)( ( (a_ | b_)>>>16 ) &4294967295l);
    }
}
