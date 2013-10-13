package com.tjh.swivel.config;

import com.tjh.swivel.config.model.Stub;
import com.tjh.swivel.config.model.Then;
import com.tjh.swivel.config.model.When;

import java.io.IOException;

import static vanderbilt.util.Validators.notNull;

/**
 * Builder to help configure a Stub. Generally returned by SwivelConfigurer#when
 */
public class StubConfigurer implements ConfigurationElement {
    private final SwivelConfigurer swivelConfigurer;
    private String description;
    private When when;
    private Then then;

    /**
     * Construct a Stubconfigurer with a SwivelConfigurer
     *
     * @param swivelConfigurer SwivelConfigurer
     */
    public StubConfigurer(SwivelConfigurer swivelConfigurer) {
        this.swivelConfigurer = notNull("swivelConfigurer", swivelConfigurer);
    }

    /**
     * Construct a StubConfigurer with a SwivelConfigurer and a When component
     *
     * @param swivelConfigurer StubConfigurer
     * @param when             When
     */
    public StubConfigurer(SwivelConfigurer swivelConfigurer, When when) {
        this(swivelConfigurer);
        setWhen(when);
    }

    //<editor-fold desc="builder">

    /**
     * Builder pattern - capture the description of the stub and return <code>this</code>
     *
     * @param description - description of the stub being constructed
     * @return <code>this</code>
     */
    public StubConfigurer describe(String description) {
        setDescription(description);
        return this;
    }

    /**
     * Builder pattern - capture the when component of the stub and return <code>this</code>
     *
     * @param when - the When component of the stub being constructed
     * @return <code>this</code>
     */
    public StubConfigurer when(When when) {
        setWhen(when);
        return this;
    }

    /**
     * Builder pattern - capture the then component of the stub and return <code>this</code>
     *
     * @param then - the Then component of the stub being constructed
     * @return <code>this</code>
     * @throws IOException
     */
    public StubConfigurer then(Then then) throws IOException {
        setThen(then);
        return this;
    }
    //</editor-fold>

    @Override
    public int configure() throws IOException {
        return swivelConfigurer.configure(new Stub(description, when, then));
    }

    //<editor-fold desc="Object">

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StubConfigurer)) return false;

        StubConfigurer that = (StubConfigurer) o;

        return swivelConfigurer.equals(that.swivelConfigurer)
                && !(description != null ? !description.equals(that.description) : that.description != null)
                && !(then != null ? !then.equals(that.then) : that.then != null) && when.equals(that.when);
    }

    @Override
    public int hashCode() {
        int result = swivelConfigurer.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + when.hashCode();
        result = 31 * result + (then != null ? then.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StubConfigurer{");
        sb.append("swivelConfigurer=").append(swivelConfigurer);
        sb.append(", description='").append(description).append('\'');
        sb.append(", when=").append(when);
        sb.append(", then=").append(then);
        sb.append('}');
        return sb.toString();
    }
    //</editor-fold>

    //<editor-fold desc="bean">
    public SwivelConfigurer getSwivelConfigurer() { return swivelConfigurer; }

    public When getWhen() { return when; }

    public void setWhen(When when) { this.when = notNull("when", when); }

    public Then getThen() { return then; }

    public void setThen(Then then) { this.then = notNull("then", then); }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = notNull("description", description); }
    //</editor-fold>
}
